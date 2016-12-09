package io.github.dector.tlamp

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import io.github.dector.tlamp.common.selectCentralItem
import io.github.dector.tlamp.common.selectItemAtPosition
import io.github.dector.tlamp.content.ContentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initContent()
        initBottomNavigation()
    }

    fun initBottomNavigation() {
        bottom_navigation.setOnNavigationItemSelectedListener {
            content_pager.currentItem = when (it.itemId) {
                R.id.main_gradient -> ContentPagerAdapter.ITEM_GRADIENT
                R.id.main_light -> ContentPagerAdapter.ITEM_LIGHT
                R.id.main_candle -> ContentPagerAdapter.ITEM_CANDLE
                else -> ContentPagerAdapter.ITEM_DEFAULT
            }

            true
        }

        bottom_navigation.selectCentralItem()
    }

    fun initContent() {
        content_pager.adapter = ContentPagerAdapter(supportFragmentManager)
        content_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) { bottom_navigation.selectItemAtPosition(position) }
        })
    }
}

