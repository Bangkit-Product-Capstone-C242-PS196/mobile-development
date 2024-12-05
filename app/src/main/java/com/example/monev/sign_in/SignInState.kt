package com.example.monev.sign_in

data class SignInState(
    val isSignInSuccesful: Boolean = false,
    val signInError: String? = null
)