package com.hyunwookshin.dock2

import android.net.Uri

object Oidc {
    const val REGION = "us-east-2"
    const val CLIENT_ID = "6jqg6fh1arrn7q22qjkcdohdgm"
    const val DOMAIN = "us-east-2yx5yfkshr.auth.us-east-2.amazoncognito.com"

    val DISCOVERY_URI: Uri =
        Uri.parse("https://$DOMAIN/.well-known/openid-configuration")

    val REDIRECT_URI: Uri = Uri.parse("dock2://callback")

    val SCOPES = listOf("openid", "email")
}
