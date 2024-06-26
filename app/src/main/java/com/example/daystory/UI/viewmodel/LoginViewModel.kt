package com.example.daystory.UI.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.daystory.api.model.UserLogin
import com.example.daystory.api.service.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel(private val application: Application): AndroidViewModel(application) {

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    fun loginValidateFields(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            _emailError.value = "Email alanı boş bırakılamaz"
            isValid = false
        }else if (email.length < 3 ) {
            _emailError.value = "Email en az 3 karakter olmalıdır"
            isValid = false
        }else if (email.length > 50) {
            _emailError.value = "Email en fazla 50 karakter olmalıdır"
            isValid = false
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = "Geçerli bir E-Mail adresi giriniz"
            isValid = false
        }else {
            _emailError.value = null
        }

        val passwordPattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{7,}$")

        if (password.isEmpty()) {
            _passwordError.value = "Şifre alanı boş bırakılamaz"
            isValid = false
        }else if (!passwordPattern.matches(password)) {
            _passwordError.value = "Şifre en az 7 karakter olmalı, 1 büyük harf, 1 küçük harf, 1 özel karakter ve 1 sayı içermeli"
            isValid = false
        }else if (password.length > 50) {
            _passwordError.value = "Şifre en fazla 50 karakter olmalıdır"
            isValid = false
        }else {
            _passwordError.value = null
        }

        return isValid
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userApi.login(UserLogin(email, password))
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val token = responseBody?.data?.token ?: ""
                    saveToken(token)
                    _loginResult.value = Result.success(token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = when (response.code()) {
                        404 -> "Kullanıcı bulunamadı"
                        401 -> "Şifreniz yanlış lütfen tekrar deneyin"
                        500 -> "Sunucu hatası: $errorBody"
                        else -> "Bilinmeyen hata: $errorBody"
                    }
                    _loginResult.value = Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("auth_token", token).apply()
        Log.d("LoginViewModel", "Token saved: $token")
    }
}