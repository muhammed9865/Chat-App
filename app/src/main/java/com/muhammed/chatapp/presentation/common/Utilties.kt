package com.muhammed.chatapp.presentation.common

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.Shimmer.Direction.RIGHT_TO_LEFT
import com.facebook.shimmer.ShimmerDrawable
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.muhammed.chatapp.R
import com.muhammed.chatapp.presentation.state.ValidationState
import java.text.SimpleDateFormat
import java.util.*


fun View.hideKeyboard() {
    val imm: InputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm: InputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun View.showError(error: String) {
    Snackbar.make(this, error, Snackbar.LENGTH_LONG)
        .setBackgroundTint(context.getColor(R.color.error))
        .setTextColor(context.getColor(R.color.white))
        .setAnimationMode(ANIMATION_MODE_SLIDE)
        .show()
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAnimationMode(ANIMATION_MODE_SLIDE)
        .show()
}

fun Long.toDateAsString(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val date = Date(this)
    return sdf.format(date)
}

fun toggleAuthError(
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

fun toggleButtonAvailabilityOnAuth(
    email: EditText? = null,
    nickname: EditText? = null,
    password: EditText? = null,
    repeatedPassword: EditText? = null,
    button: Button
) {
    val fields = mutableListOf<EditText>()
    email?.let { fields.add(it) }
    password?.let { fields.add(it) }
    repeatedPassword?.let { fields.add(it) }
    nickname?.let { fields.add(it) }

    val isAnyEmptyField = fields.any { it.text.isNullOrEmpty() }

    button.apply {
        isEnabled = !isAnyEmptyField
        isFocusable = !isAnyEmptyField
    }
}

fun DialogFragment.hideDialog() {
    if (isVisible) {
        dismiss()

    }
}

fun DialogFragment.showDialog(manager: FragmentManager, tag: String?) {
    if (isAdded) {
        Log.d("LoadingDialog", "showDialog: showing")
        dialog?.show()
    }else {
        Log.d("LoadingDialog", "hideDialog: hiding")
        show(manager, tag)
    }
}


fun ImageView.loadImage(url: String) {
    val shimmer = Shimmer.AlphaHighlightBuilder()
        .setBaseAlpha(0.6f)
        .setHighlightAlpha(0.7f)
        .setDuration(1000)
        .setDirection(RIGHT_TO_LEFT)
        .setAutoStart(true)
        .build()

    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

    Glide.with(context)
        .load(url)
        .placeholder(shimmerDrawable)
        .error(R.drawable.ic_baseline_broken_image_24)
        .into(this)

}

