package io.github.dector.tlamp.connection

interface ILampDataLoader {

    fun getCurrentColor(onSuccess: (Int) -> Unit,
                        onFail: () -> Unit = {})

    fun setStaticColor(color: Int,
                       onSuccess: () -> Unit = {},
                       onFail: () -> Unit = {})
}

class MockLampDataLoader : ILampDataLoader {

    private var color = 0xFF00FF00.toInt()

    override fun getCurrentColor(onSuccess: (Int) -> Unit, onFail: () -> Unit) {
        onSuccess(color)
    }

    override fun setStaticColor(color: Int, onSuccess: () -> Unit, onFail: () -> Unit) {
        this.color = color
        onSuccess()
    }
}