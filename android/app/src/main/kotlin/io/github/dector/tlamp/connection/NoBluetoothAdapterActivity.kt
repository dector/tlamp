package io.github.dector.tlamp.connection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.dector.tlamp.R
import kotlinx.android.synthetic.main.activity_no_bluetooth_adapter.*

class NoBluetoothAdapterActivity : Activity() {

    companion object {

        fun newIntent(context: Context) = Intent(context, NoBluetoothAdapterActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_bluetooth_adapter)

        no_bluetooth_confirm_button.setOnClickListener { finish() }
    }
}