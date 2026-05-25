package com.feature.authentication.data.oauth

object GoogleOAuthConfig {
    internal const val GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"
    internal const val GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token"
    internal const val REDIRECT_HOST = "localhost"
    internal const val CALLBACK_PATH = "/callback"
    internal const val AUTH_TIMEOUT_SECONDS = 180L
    
    internal fun redirectUrl(port: Int): String = "http://$REDIRECT_HOST:$port$CALLBACK_PATH"
}