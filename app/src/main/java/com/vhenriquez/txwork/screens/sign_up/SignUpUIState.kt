package com.vhenriquez.txwork.screens.sign_up

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false

)