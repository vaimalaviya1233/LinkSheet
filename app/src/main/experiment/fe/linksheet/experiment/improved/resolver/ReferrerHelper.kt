package fe.linksheet.experiment.improved.resolver

import android.content.Context
import android.net.Uri

object ReferrerHelper {
    private const val APP_SCHEME: String = "android-app"

    fun getReferringPackage(uri: Uri?): String? {
        return if (uri?.scheme == APP_SCHEME) uri.host else null
    }

    fun createReferrer(context: Context): Uri {
        return Uri.fromParts(APP_SCHEME, context.packageName, null)
    }
}