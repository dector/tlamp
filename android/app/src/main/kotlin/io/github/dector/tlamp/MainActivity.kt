package io.github.dector.tlamp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import io.github.dector.tlamp.color_wheel.solid
import io.github.dector.tlamp.common.selectCentralItem
import io.github.dector.tlamp.common.selectItemAtPosition
import io.github.dector.tlamp.connection.ILampDataLoader
import io.github.dector.tlamp.connection.MockLampDataLoader
import io.github.dector.tlamp.content.ContentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val lampDataLoader = MockLampDataLoader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initContent()
        initBottomNavigation()

        // Debug
        supportFragmentManager.beginTransaction()
                .add(R.id.main_root, DebugLampFragment(lampDataLoader))
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val isConnected = BTManager.isConnected()

        menu?.findItem(R.id.main_bluetooth)?.isVisible = !isConnected
        menu?.findItem(R.id.main_disconnect)?.isVisible = isConnected

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.main_bluetooth -> { connectToLamp(); true }
        R.id.main_disconnect -> { disconnectFromLamp(); true }
        else -> false
    }

    private fun connectToLamp() {
        val connected = BTManager.connect()

        val notificationText = if (connected) R.string.bluetooth_lamp_connected else R.string.bluetooth_lamp_not_connected
        Snackbar.make(main_root, notificationText, Snackbar.LENGTH_LONG).show()

        invalidateOptionsMenu()
    }

    private fun disconnectFromLamp() {
        BTManager.disconnect()

        invalidateOptionsMenu()
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

object BTManager : ILampDataLoader {

    private var socket: BluetoothSocket? = null

    fun connect(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val device = adapter.bondedDevices.filter { it.name == "tLamp" }.firstOrNull()
                ?: return false

        val uuid = device.uuids?.firstOrNull()?.uuid ?: UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val socket = device.createInsecureRfcommSocketToServiceRecord(uuid)

        try {
            socket.connect()
        } catch (e: Exception) {
            try {
                socket.close()
            } catch (e2: Exception) {}

            Log.e("Connection", "Socket not connected", e)
            return false
        }
        this.socket = socket

        return true
    }

    fun disconnect() {
        try {
            socket?.close()
        } catch (e: Exception) {
        } finally {
            socket = null
        }
    }

    override fun getCurrentColor(onSuccess: (Int) -> Unit, onFail: () -> Unit) {
    }

    override fun setStaticColor(color: Int, onSuccess: () -> Unit, onFail: () -> Unit) {
        if (!isConnected()) {
            onFail()
            return
        }

        val colorString = Integer.toHexString(color)
        val command = "SET #$colorString"

        Log.d("Command", "Sending $command to ${socket?.remoteDevice?.name}")

        try {
            socket?.outputStream?.write(command.toByteArray())
            onSuccess()
        } catch (e: Exception) {
            onFail()
        }
    }

    override fun activateGradient(onSuccess: () -> Unit, onFail: () -> Unit) {
    }

    override fun isConnected() = socket?.isConnected ?: false
}
