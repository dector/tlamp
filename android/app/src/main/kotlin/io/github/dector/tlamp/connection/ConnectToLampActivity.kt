package io.github.dector.tlamp.connection

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import io.github.dector.tlamp.common.isNotNull

class ConnectToLampActivity : Activity() {

    companion object {

        fun newIntent(context: Context) =
                Intent(context, ConnectToLampActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasAdapter()) {
            if (hasBluetoothPermission()) {
                startActivity(BluetoothDevicesListActivity.newIntent(this))
                //connectToLamp()
                finish()
            } else {
                startActivity(RequestBluetoothPermissionActivity.newIntent(this))
            }
        } else {
            startActivity(NoBluetoothAdapterActivity.newIntent(this))
            finish()
        }
    }

    private fun hasAdapter() = BluetoothAdapter.getDefaultAdapter().isNotNull()

    private fun hasBluetoothPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
    }

    /*private fun connectToLamp() {
        val connected = BTManager.connectAsync()

        val notificationText = if (connected) R.string.bluetooth_lamp_connected else R.string.bluetooth_lamp_not_connected
        //Snackbar.make(main_root, notificationText, Snackbar.LENGTH_LONG).show()
        Toast.makeText(applicationContext, notificationText, Toast.LENGTH_SHORT).show()
    }*/
}