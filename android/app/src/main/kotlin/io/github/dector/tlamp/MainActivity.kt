package io.github.dector.tlamp

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.dector.tlamp.color_wheel.solid
import io.github.dector.tlamp.common.selectCentralItem
import io.github.dector.tlamp.common.selectItemAtPosition
import io.github.dector.tlamp.connection.MockLampDataLoader
import io.github.dector.tlamp.content.ContentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val lampDataLoader = MockLampDataLoader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initContent()
        initBottomNavigation()

        // Debug
        supportFragmentManager.beginTransaction()
                .add(R.id.activity_main, DebugLampFragment(lampDataLoader))
                .commit()
    }

    private fun initBottomNavigation() {
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

    private fun initContent() {
        content_pager.adapter = ContentPagerAdapter(supportFragmentManager, lampDataLoader)
        content_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) { bottom_navigation.selectItemAtPosition(position) }
        })
    }
}

class DebugLampFragment(private val lampDataLoader: MockLampDataLoader) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View(context, null).apply {
            layoutParams = ViewGroup.LayoutParams(64, 64)
            setBackgroundColor(Color.BLACK)

            lampDataLoader.addColorListener(object : MockLampDataLoader.IColorListener {
                override fun onColorChanged(color: Int) {
                    setBackgroundColor(color.solid())
                }
            })
        }
    }
}

