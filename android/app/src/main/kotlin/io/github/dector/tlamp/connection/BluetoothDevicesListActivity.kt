package io.github.dector.tlamp.connection

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import io.github.dector.tlamp.BTManager
import io.github.dector.tlamp.R
import kotlinx.android.synthetic.main.activity_bluetooth_devices_list.*

class BluetoothDevicesListActivity : Activity() {

    private val REQUEST_CODE_ENABLE_BLUETOOTH = 1

    companion object {

        fun newIntent(context: Context) = Intent(context, BluetoothDevicesListActivity::class.java)
    }

    private val adapter = DevicesListAdapter()

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_devices_list)

        adapter.selectionListener = object : DevicesListAdapter.OnSelectedListener {

            override fun onDeviceSelected(device: Device) {
                connectToDevice(device)
            }
        }

        bluetooth_devices_list.layoutManager = LinearLayoutManager(this)
        bluetooth_devices_list.setHasFixedSize(true)
        bluetooth_devices_list.adapter = adapter
        bluetooth_devices_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        progressDialog = ProgressDialog(this@BluetoothDevicesListActivity).apply {
            isIndeterminate = true
            setMessage("Connecting")
        }

        initContentState()
    }

    private fun connectToDevice(device: Device) {
        progressDialog.show()
        BTManager.connectAsync(device.address, { success ->
            progressDialog.dismiss()

            if (success) {
                finish()
                Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
            } else {
                Snackbar.make(bluetooth_devices_content_container, "Connection Error", Snackbar.LENGTH_SHORT)
                        .setAction("Retry") { Handler().post { connectToDevice(device) } }
                        .show()
            }
        })
    }

    private fun initContentState() {
        val bluetoothEnabled = BluetoothAdapter.getDefaultAdapter().isEnabled
        bluetooth_devices_content_container.visibility = if (bluetoothEnabled) View.VISIBLE else View.GONE
        bluetooth_devices_turn_on_container.visibility = if (bluetoothEnabled) View.GONE else View.VISIBLE
        bluetooth_devices_turn_on_bluetooth_button.setOnClickListener {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    REQUEST_CODE_ENABLE_BLUETOOTH)
        }

        loadPairedDevices()
    }

    private fun loadPairedDevices() {
        adapter.setItems(BluetoothAdapter.getDefaultAdapter().bondedDevices.map { Device(it.name, it.address) })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BLUETOOTH -> initContentState()
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

data class Device(val name: String, val address: String)

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val name = view.findViewById(R.id.device_name) as TextView
}

class DevicesListAdapter : RecyclerView.Adapter<ViewHolder>() {

    interface OnSelectedListener {

        fun onDeviceSelected(device: Device)
    }

    private val items: MutableList<Device> = mutableListOf()

    var selectionListener: OnSelectedListener? = null

    fun setItems(items: List<Device>) {
        this.items.clear()
        this.items.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.view_devices_list_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val device = items[position]
        holder?.name?.text = device.name
        holder?.itemView?.setOnClickListener { selectionListener?.onDeviceSelected(device) }
    }

    override fun getItemCount() = items.size
}