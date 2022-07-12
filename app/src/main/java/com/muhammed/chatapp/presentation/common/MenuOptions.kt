package com.muhammed.chatapp.presentation.common

import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.fragment.app.FragmentActivity
import com.muhammed.chatapp.R
import com.muhammed.chatapp.presentation.dialogs.SignOutDialog

class MenuOptions(private val fragmentActivity: FragmentActivity, attachTo: View, @MenuRes private val menu: Int) {
    private var popUp: PopupMenu = PopupMenu(fragmentActivity, attachTo)

    fun showMenu() {
        popUp.menuInflater.inflate(menu, popUp.menu)
        popUp.show()
    }

    fun onSignOutSelected(onSignOut:() -> Unit) {
        popUp.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_signOut -> {
                    val sod = SignOutDialog()
                    sod.setOnSignOutListener(onSignOut)
                    sod.show(fragmentActivity.supportFragmentManager, null)
                    true
                }
                else -> false
            }
        }
    }

}