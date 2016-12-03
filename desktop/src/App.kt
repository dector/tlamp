import javafx.application.Application
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.layout.GridPane
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

        val grid = GridPane().apply {
            alignment = Pos.CENTER
            padding = Insets(8.0, 8.0, 8.0, 8.0)
            hgap = 8.0
            vgap = 8.0
            style = "-fx-background-color: #aeaeae"

            root.children.add(this)
        }

        Color.values().forEach { color ->
            Button("        ").apply {
                style = "-fx-background-color: ${color.value}"
                setOnMouseClicked { sendSetColor(color) }

                val colorIndex = Color.values().indexOf(color)
                val rowLength = 6
                val row = colorIndex / rowLength
                val column = colorIndex - row * rowLength
                grid.add(this, column + 1, row + 1)
            }
        }

        val scene = Scene(root, 400.0, 200.0)
        primaryStage?.scene = scene
        primaryStage?.title = "tLamp"
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
    RED         ("#FF0000"),
    CYAN        ("#00FFFF"),
    BLUE        ("#0000FF"),
    DARK_BLUE   ("#0000A0"),
    PURPLE      ("#800080"),
    YELLOW      ("#FFFF00"),
    LIME        ("#00FF00"),
    FUCHSIA     ("#FF00FF"),
    OLIVE       ("#808000"),
    GREEN       ("#008000"),
    MAROON      ("#800000"),
    BROWN       ("#A52A2A"),
    ORANGE      ("#FFA500"),
    BLACK       ("#000000"),
    GREY        ("#808080"),
    SILVER      ("#C0C0C0"),
    WHITE       ("#FFFFFF")
}

fun main(vararg args: String) {
    Application.launch(TLamp::class.java)
}