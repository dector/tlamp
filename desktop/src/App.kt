import javafx.application.Application
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import jssc.SerialPort
import jssc.SerialPortList
import kotlin.system.exitProcess

class TLamp : Application() {

    val portsList = FXCollections.observableArrayList<String>()
    var currentPort: SerialPort? = null

    override fun start(primaryStage: Stage?) {
        val root = VBox().apply {
            alignment = Pos.CENTER
            spacing = 16.0
        }

        HBox().apply {
            alignment = Pos.CENTER
            spacing = 16.0

            ComboBox(portsList).apply {
                valueProperty().addListener { o, old, new -> if (old != new) newPortSelected(new) }
            }.let { children.add(it) }

            Button("Update").let { children.add(it) }

            root.children.add(this)
        }

        Button("Red").apply {
            setOnMouseClicked { sendSetColor(Color.RED) }
            root.children.add(this)
        }

        Button("Green").apply {
            setOnMouseClicked { sendSetColor(Color.GREEN) }
            root.children.add(this)
        }

        Button("Blue").apply {
            setOnMouseClicked { sendSetColor(Color.BLUE) }
            root.children.add(this)
        }

        val scene = Scene(root, 300.0, 200.0)
        primaryStage?.scene = scene
        primaryStage?.title = "TLamp"
        primaryStage?.show()
        primaryStage?.setOnCloseRequest {
            currentPort?.closePort()
            exitProcess(0)
        }

        updatePortsList()
    }

    fun updatePortsList() {
        val ports = SerialPortList.getPortNames()

        portsList.setAll(*ports)
    }

    fun newPortSelected(name: String) {
        val port = SerialPort(name)
        if (! port.openPort()) {
            println("Can't open port $name")
            return
        }

        println("Port $name opened. Configuring")

        port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
        port.addEventListener {
            if (it.isRXCHAR && it.eventValue > 0) {
                onDataReceived(port, it.eventValue)
            }
        }
        currentPort = port
    }

    fun onDataReceived(port: SerialPort, amount: Int) {
        val data = port.readString(amount)

        print(">> $data")
    }

    fun sendSetColor(color: Color) {
        val port =  currentPort ?: return

        val data = "SET " + color.value
        port.writeString(data)

        println("<< $data")
    }

}

enum class Color(val value: String) {
    RED     ("#FF0000"),
    GREEN   ("#00FF00"),
    BLUE    ("#0000FF")
}

fun main(vararg args: String) {
    Application.launch(TLamp::class.java)
}