package io.github.dector.tlamp.content

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import io.github.dector.tlamp.candle.CandleFragment
import io.github.dector.tlamp.gradient.GradientFragment
import io.github.dector.tlamp.light.LightFragment

class ContentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    companion object {
        val ITEM_GRADIENT = 0
        val ITEM_LIGHT = 1
        val ITEM_CANDLE = 2

        val ITEM_DEFAULT = ITEM_LIGHT
    }

    private val ITEMS_COUNT = 3

    override fun getItem(position: Int) = when (position) {
        ITEM_GRADIENT -> GradientFragment()
        ITEM_LIGHT -> LightFragment()
        ITEM_CANDLE -> CandleFragment()
        else -> null
    }

    override fun getCount() = ITEMS_COUNT
}