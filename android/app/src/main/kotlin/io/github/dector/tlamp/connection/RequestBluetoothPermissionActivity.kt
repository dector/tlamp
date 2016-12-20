package io.github.dector.tlamp.connection

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import io.github.dector.tlamp.R
import kotlinx.android.synthetic.main.activity_no_bt_permission.*

class RequestBluetoothPermissionActivity : Activity() {

    companion object {

        fun newIntent(context: Context) =
                Intent(context, RequestBluetoothPermissionActivity::class.java)
    }

    private val REQUEST_CODE_BT_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_bt_permission)

        request_permission_button.setOnClickListener {
            val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH)
            if (shouldShowRationale) {
                Snackbar.make(request_permission_root, "Rationale", Snackbar.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.BLUETOOTH), REQUEST_CODE_BT_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_BT_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    // Denied
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}