package com.mytiki.publish.client.ui.license

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mytiki.publish.client.TikiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LicenseViewModel(): ViewModel() {

    private val _isLicensed = mutableStateOf(false)
    val isLicensed = _isLicensed

    fun updateIsLicensed(){
        _isLicensed.value = TikiClient.license.isLicensed()
        Log.d("*************", "viewModelState: ${isLicensed.value}")
    }

    fun acceptLicense() {
        TikiClient.license.accept()
        updateIsLicensed()
    }
    fun declineLicense() {
        TikiClient.license.decline()
        updateIsLicensed()
    }

    fun estimate() = TikiClient.license.estimate()

    fun terms() = TikiClient.license.terms()
}