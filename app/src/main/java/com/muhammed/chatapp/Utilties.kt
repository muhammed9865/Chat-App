package com.muhammed.chatapp

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.muhammed.chatapp.presentation.state.ValidationState

fun View.hideKeyboard() {
    val imm: InputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm: InputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.showError(error: String) {
    Snackbar.make(this, error, Snackbar.LENGTH_LONG)
        .setBackgroundTint(context.getColor(R.color.error))
        .setTextColor(context.getColor(R.color.white))
        .setAnimationMode(ANIMATION_MODE_SLIDE)
        .show()
}

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAnimationMode(ANIMATION_MODE_SLIDE)
        .show()
}

fun Fragment.toggleAuthError(
    email: EditText? = null,
    nickname: EditText? = null,
    password: EditText? = null,
    repeatedPassword: EditText? = null,
    validationState: ValidationState
) {
    with(validationState) {
        email?.error = emailError
        nickname?.error = nicknameError
        password?.error = passwordError
        repeatedPassword?.error = repeatedPasswordError
    }
}