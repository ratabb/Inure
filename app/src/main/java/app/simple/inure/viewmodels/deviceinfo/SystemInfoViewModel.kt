package app.simple.inure.viewmodels.deviceinfo

import android.app.Application
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.text.Spannable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.simple.inure.R
import app.simple.inure.extension.viewmodels.WrappedViewModel
import app.simple.inure.util.DeviceUtils
import app.simple.inure.util.NumberUtils
import app.simple.inure.util.SDKHelper
import app.simple.inure.util.StringUtils.applySecondaryTextColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SystemInfoViewModel(application: Application) : WrappedViewModel(application) {
    private val information: MutableLiveData<ArrayList<Pair<String, Spannable>>> by lazy {
        MutableLiveData<ArrayList<Pair<String, Spannable>>>().also {
            viewModelScope.launch(Dispatchers.IO) {
                loadInformation()
            }
        }
    }

    fun getInformation(): LiveData<ArrayList<Pair<String, Spannable>>> {
        return information
    }

    private fun loadInformation() {
        viewModelScope.launch(Dispatchers.Default) {
            information.postValue(arrayListOf(
                    getAndroidVersion(),
                    getHardwareName(),
                    getSecurityUpdate(),
                    getKernelVersion(),
                    getUser(),
                    getManufacturer(),
                    getModel(),
                    getBoard(),
                    getBootloader(),
                    getFingerprint(),
                    getUSBDebugState(),
                    getUpTime()
            ))
        }
    }

    private fun getAndroidVersion(): Pair<String, Spannable> {
        return Pair(getString(R.string.android_version),
                    SDKHelper.getSdkTitle(Build.VERSION.SDK_INT).applySecondaryTextColor())
    }

    private fun getHardwareName(): Pair<String, Spannable> {
        return Pair(getString(R.string.hardware),
                    Build.HARDWARE.applySecondaryTextColor())
    }

    private fun getSecurityUpdate(): Pair<String, Spannable> {
        return Pair(getString(R.string.security_update),
                    Build.VERSION.SECURITY_PATCH.applySecondaryTextColor())
    }

    private fun getKernelVersion(): Pair<String, Spannable> {
        return Pair(getString(R.string.kernel_version),
                    DeviceUtils.readKernelVersion()!!.applySecondaryTextColor())
    }

    private fun getUser(): Pair<String, Spannable> {
        return Pair(getString(R.string.user),
                    Build.USER.applySecondaryTextColor())
    }

    private fun getManufacturer(): Pair<String, Spannable> {
        return Pair(getString(R.string.manufacturer),
                    Build.MANUFACTURER.applySecondaryTextColor())
    }

    private fun getModel(): Pair<String, Spannable> {
        return Pair(getString(R.string.model),
                    Build.MODEL.applySecondaryTextColor())
    }

    private fun getBoard(): Pair<String, Spannable> {
        return Pair(getString(R.string.board),
                    Build.BOARD.applySecondaryTextColor())
    }

    private fun getBootloader(): Pair<String, Spannable> {
        return Pair("Bootloader",
                    Build.BOOTLOADER.applySecondaryTextColor())
    }

    private fun getFingerprint(): Pair<String, Spannable> {
        return Pair(getString(R.string.fingerprint),
                    Build.FINGERPRINT.applySecondaryTextColor())
    }

    private fun getUSBDebugState(): Pair<String, Spannable> {
        val s = if (Settings.Secure.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1) {
            getString(R.string.enabled)
        } else {
            getString(R.string.disabled)
        }

        return Pair("USB Debugging", s.applySecondaryTextColor())
    }

    private fun getUpTime(): Pair<String, Spannable> {
        return Pair(getString(R.string.up_time),
                    NumberUtils.getFormattedTime(SystemClock.uptimeMillis()).applySecondaryTextColor())
    }
}