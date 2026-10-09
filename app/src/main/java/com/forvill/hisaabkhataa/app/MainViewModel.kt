package com.forvill.hisaabkhataa.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.forvill.hisaabkhataa.core.i18n.AppLanguage
import com.forvill.hisaabkhataa.core.i18n.AppStrings
import com.forvill.hisaabkhataa.core.i18n.appStrings
import com.forvill.hisaabkhataa.core.i18n.languageFromName
import com.forvill.hisaabkhataa.core.model.*
import com.forvill.hisaabkhataa.data.repository.ProductionLedgerRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProductionLedgerRepository(application)

    private val _settings = MutableLiveData(repository.getAppSettings())
    val settings: LiveData<AppSettings> = _settings

    private val _selectedLanguage = MutableLiveData(_settings.value?.selectedLanguage ?: "English")
    val selectedLanguage: LiveData<String> = _selectedLanguage

    private val _businessProfile = MutableLiveData(repository.getBusinessProfile())
    val businessProfile: LiveData<BusinessProfile> = _businessProfile

    private val _parties = MutableLiveData(repository.getParties())
    val parties: LiveData<List<Party>> = _parties

    private val _expenses = MutableLiveData(repository.getExpenses())
    val expenses: LiveData<List<TransactionExpense>> = _expenses

    val onboardingPages = repository.getOnboardingPages()

    fun getStrings(): AppStrings = appStrings(languageFromName(_selectedLanguage.value ?: "English"))

    fun updateSelectedLanguage(language: String) {
        val newSettings = repository.saveAppSettings(_settings.value?.copy(selectedLanguage = language) ?: AppSettings(selectedLanguage = language))
        _settings.value = newSettings
        _selectedLanguage.value = language
    }

    fun updateProfile(profile: BusinessProfile) {
        _businessProfile.value = repository.saveBusinessProfile(profile)
    }

    // Add other functions as needed from AppState...
    
    fun refreshParties() {
        _parties.value = repository.getParties()
    }

    fun refreshExpenses() {
        _expenses.value = repository.getExpenses()
    }
}
