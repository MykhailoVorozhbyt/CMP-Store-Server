//package com.feature.authentication.presentation.social_media
//
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//@Composable
//fun SocialMediaStartActivityForResult(
//    key: Any,
//    onLauncher: (ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) -> Unit
//) {
//    val googleSignInLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartIntentSenderForResult()
//    ) { /* No-op: Credential Manager does not use an IntentSender result for Google ID tokens */ }
//
//    LaunchedEffect(key) {
//        onLauncher.invoke(googleSignInLauncher)
//    }
//}
//
//@Suppress("TooGenericExceptionCaught", "InjectDispatcher")
//fun startSignFlow(
//    activity: Activity,
//    onResultReceived: (String) -> Unit,
//    accountNotFound: () -> Unit
//) {
//    var scope: CoroutineScope? = CoroutineScope(Dispatchers.Main)
//
//    val credentialManager = CredentialManager.create(activity)
//    val googleIdOption = GetGoogleIdOption.Builder()
//        .setServerClientId(activity.getString(R.string.default_web_client_id))
//        .setFilterByAuthorizedAccounts(true) // prefer previously authorized accounts if any
//        .build()
//
//    val request = GetCredentialRequest.Builder()
//        .addCredentialOption(googleIdOption)
//        .build()
//
//    scope?.launch {
//        try {
//            val result = credentialManager.getCredential(
//                request = request,
//                context = activity
//            )
//
//            val credential = result.credential
//            if (credential is CustomCredential &&
//                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
//            ) {
//                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
//                val idToken = googleIdTokenCredential.idToken
//                if (idToken.isEmpty()) {
//                    ErrorLogger.logError("GAuthorizationClient: idToken is empty")
//                    accountNotFound.invoke()
//                } else {
//                    onResultReceived.invoke(idToken)
//                }
//            } else {
//                ErrorLogger.logError("GAuthorizationClient: Unsupported credential type: ${credential::class.java}")
//                accountNotFound.invoke()
//            }
//        } catch (e: NoCredentialException) {
//            ErrorLogger.logError("GAuthorizationClient: No credentials available")
//            accountNotFound.invoke()
//        } catch (e: Exception) {
//            ErrorLogger.logError(e)
//            accountNotFound.invoke()
//        } finally {
//            scope = null
//        }
//    }
//}
