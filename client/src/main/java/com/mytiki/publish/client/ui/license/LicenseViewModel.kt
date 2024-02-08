package com.mytiki.publish.client.ui.license

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mytiki.publish.client.TikiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LicenseViewModel(): ViewModel() {

    private val _isLicensed = MutableStateFlow(false)
    val isLicensed = _isLicensed.asStateFlow()

    init {
        _isLicensed.value = TikiClient.license.isLicensed()
    }

    fun acceptLicense() {
        TikiClient.license.accept()
        _isLicensed.value = TikiClient.license.isLicensed()
    }
    fun declineLicense() {
        TikiClient.license.decline()
        _isLicensed.value = TikiClient.license.isLicensed()
    }

    fun estimate() = TikiClient.license.estimate()
    
    fun terms() = TikiClient.license.terms()
}