package io.github.dector.tlamp.connection

import java.util.*

interface ILampDataLoader {

    fun getCurrentColor(onSuccess: (Int) -> Unit,
                        onFail: () -> Unit = {})

    fun setStaticColor(color: Int,
                       onSuccess: () -> Unit = {},
                       onFail: () -> Unit = {})
}

class MockLampDataLoader : ILampDataLoader {

    interface IColorListener {
        fun onColorChanged(color: Int)
    }

    private val colorListeners = ArrayList<IColorListener>()

    private var color = 0xFF00FF00.toInt()

    override fun getCurrentColor(onSuccess: (Int) -> Unit, onFail: () -> Unit) {
        onSuccess(color)
    }

    override fun setStaticColor(color: Int, onSuccess: () -> Unit, onFail: () -> Unit) {
        this.color = color

        colorListeners.forEach { it.onColorChanged(color) }

        onSuccess()
    }

    fun addColorListener(listener: IColorListener) {
        colorListeners.add(listener)
        listener.onColorChanged(color)
    }
}