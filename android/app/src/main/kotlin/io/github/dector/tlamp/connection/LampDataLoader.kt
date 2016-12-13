package io.github.dector.tlamp.connection

import io.github.dector.tlamp.BTManager
import java.util.*

interface ILampDataLoader {

    fun isConnected(): Boolean

    fun getCurrentColor(onSuccess: (Int) -> Unit,
                        onFail: () -> Unit = {})

    fun setStaticColor(color: Int,
                       onSuccess: () -> Unit = {},
                       onFail: () -> Unit = {})

    fun activateGradient(onSuccess: () -> Unit = {},
                         onFail: () -> Unit = {})
}

class MockLampDataLoader : ILampDataLoader {

    enum class State {
        COLOR, GRADIENT
    }

    interface IStateListener {
        fun onStateChanged(newState: State)
    }

    interface IColorListener {
        fun onColorChanged(color: Int)
    }

    private val stateListeners = ArrayList<IStateListener>()
    private val colorListeners = ArrayList<IColorListener>()

    private var state = State.COLOR
    private var color = 0xFF00FF00.toInt()

    val connected = false

    override fun isConnected(): Boolean {
        return connected
    }

    override fun getCurrentColor(onSuccess: (Int) -> Unit, onFail: () -> Unit) {
        onSuccess(color)
    }

    override fun setStaticColor(color: Int, onSuccess: () -> Unit, onFail: () -> Unit) {
        this.color = color
        this.state = State.COLOR

        BTManager.setStaticColor(color, onSuccess, onFail)

        stateListeners.forEach { it.onStateChanged(state) }
        colorListeners.forEach { it.onColorChanged(color) }

        onSuccess()
    }

    override fun activateGradient(onSuccess: () -> Unit, onFail: () -> Unit) {
        this.state = State.GRADIENT

        stateListeners.forEach { it.onStateChanged(state) }

        onSuccess()
    }

    fun addColorListener(listener: IColorListener, postCurrent: Boolean = true) {
        colorListeners.add(listener)

        if (postCurrent)
            listener.onColorChanged(color)
    }

    fun addStateListner(listener: IStateListener, postCurrent: Boolean = true) {
        stateListeners.add(listener)

        if (postCurrent)
            listener.onStateChanged(state)
    }
}