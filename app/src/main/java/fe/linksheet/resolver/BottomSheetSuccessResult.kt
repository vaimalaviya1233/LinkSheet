package fe.linksheet.resolver

import android.content.Intent
import android.net.Uri
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.resolver.LibRedirectResult
import fe.linksheet.module.resolver.ResolveModuleStatus
import me.saket.unfurl.UnfurlResult


sealed class BottomSheetResult(val uri: Uri?) {
    abstract class SuccessResult(uri: Uri?, val intent: Intent, val resolved: List<DisplayActivityInfo>) :
        BottomSheetResult(uri) {
        abstract fun isEmpty(): Boolean
    }

    class BottomSheetSuccessResult(
        intent: Intent,
        uri: Uri?,
        val unfurlResult: UnfurlResult?,
        referrer: Uri?,
        resolved: List<DisplayActivityInfo>,
        val filteredItem: DisplayActivityInfo?,
        val showExtended: Boolean,
        alwaysPreferred: Boolean?,
        hasSingleMatchingOption: Boolean = false,
        val resolveModuleStatus: ResolveModuleStatus,
        val libRedirectResult: LibRedirectResult? = null,
        val downloadable: DownloadCheckResult = DownloadCheckResult.NonDownloadable,
    ) : SuccessResult(uri, intent, resolved) {
        private val totalCount = resolved.size + if (filteredItem != null) 1 else 0

        private val referringPackageName =
            if (referrer?.scheme == "android-app") referrer.host else null

        val isRegularPreferredApp = alwaysPreferred == true && filteredItem != null
        val app = filteredItem ?: resolved[0]

        val hasAutoLaunchApp =
            (isRegularPreferredApp || hasSingleMatchingOption) && (referringPackageName == null || app.packageName != referringPackageName)

        override fun isEmpty(): Boolean {
            return totalCount == 0
        }
    }

    class BottomSheetNoHandlersFound(uri: Uri? = null) : BottomSheetResult(uri)

    class BottomSheetWebSearchResult(val query: String, intent: Intent, resolved: List<DisplayActivityInfo>) :
        SuccessResult(null, intent, resolved) {
        override fun isEmpty(): Boolean {
            return resolved.isEmpty()
        }
    }
}

