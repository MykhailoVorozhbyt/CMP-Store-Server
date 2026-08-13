package com.store.core.security

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class JvmSecureStorage(
    baseDir: File = File(System.getProperty("user.home"), ".yourapp"),
) : SecureStorage {
    private val keyFile = File(baseDir, "vault.key")
    private val dataFile = File(baseDir, "vault.dat")
    private val mutex = Mutex()

    init {
        baseDir.mkdirs()
    }

    private fun secretKey(): SecretKey {
        if (keyFile.exists()) return SecretKeySpec(keyFile.readBytes(), "AES")
        val key = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
        keyFile.writeBytes(key.encoded)
        restrict(keyFile)
        return key
    }

    override suspend fun putValue(key: String, value: String) = mutex.withLock {
        withContext(Dispatchers.IO) {
            val map = readAll().toMutableMap().apply { put(key, value) }
            writeAll(map)
        }
    }

    override suspend fun getValue(key: String): String? = mutex.withLock {
        withContext(Dispatchers.IO) { readAll()[key] }
    }

    override suspend fun removeByKey(key: String) = mutex.withLock {
        withContext(Dispatchers.IO) { writeAll(readAll().toMutableMap().apply { remove(key) }) }
    }

    private fun readAll(): Map<String, String> {
        if (!dataFile.exists()) return emptyMap()
        return runCatching {
            val raw = dataFile.readBytes()
            val iv = raw.copyOfRange(0, 12)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                .apply { init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv)) }
            cipher.doFinal(raw.copyOfRange(12, raw.size)).decodeToString()
                .lineSequence().mapNotNull { it.split("=", limit = 2).takeIf { p -> p.size == 2 } }
                .associate { it[0] to it[1] }
        }.getOrElse { emptyMap() }
    }

    private fun writeAll(map: Map<String, String>) {
        val plain = map.entries.joinToString("\n") { "${it.key}=${it.value}" }
        val iv = ByteArray(12).also { SECURE_RANDOM.nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            .apply { init(Cipher.ENCRYPT_MODE, secretKey(), GCMParameterSpec(128, iv)) }
        dataFile.writeBytes(iv + cipher.doFinal(plain.toByteArray()))
        restrict(dataFile)
    }

    private fun restrict(file: File) = runCatching {
        file.setReadable(false, false); file.setWritable(false, false)
        file.setReadable(true, true); file.setWritable(true, true)
    }

    companion object {
        private val SECURE_RANDOM = SecureRandom()
    }
}