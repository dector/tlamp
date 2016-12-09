package io.github.dector.tlamp.common

import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView

// Hi, Google! Still releasing not customizable components? :)
fun BottomNavigationView.selectItemAtPosition(position: Int) {
    val menuView = getChildAt(0) as BottomNavigationMenuView
    menuView.getChildAt(position).performClick()
}

fun BottomNavigationView.selectCentralItem() {
    selectItemAtPosition(menu.size() / 2)
}