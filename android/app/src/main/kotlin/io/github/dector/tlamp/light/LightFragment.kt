package io.github.dector.tlamp.light

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.dector.tlamp.R
import io.github.dector.tlamp.color_wheel.ColorWheelView
import io.github.dector.tlamp.connection.ILampDataLoader
import kotlinx.android.synthetic.main.fragment_light.*

class LightFragment(val dataLoader: ILampDataLoader) : Fragment() {

    private var selectedColor = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater?.inflate(R.layout.fragment_light, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        light_color_picker.colorSelectedListener = object : ColorWheelView.OnColorSelectedListener {

            override fun onColorSelected(color: Int) {
                selectedColor = color

                if (! areButtonsDisplayed()) {
                    displayButtons(true)
                }
            }
        }

        light_revert_button.setOnClickListener {
            reloadCurrentColor()
        }

        light_save_button.setOnClickListener {
            val newColor = selectedColor

            dataLoader.setStaticColor(newColor,
                    onSuccess = { onColorSaved(newColor) })
        }
    }

    override fun onResume() {
        super.onResume()

        reloadCurrentColor()
    }

    private fun reloadCurrentColor() {
        dataLoader.getCurrentColor(onSuccess = { onColorLoaded(it) }) // Not safe. Unknown color can be received here

        displayButtons(false)
    }

    private fun onColorLoaded(color: Int) {
        selectedColor = color
        light_color_picker.setSelectedColor(selectedColor)
    }

    private fun onColorSaved(color: Int) {
        onColorLoaded(color)
        displayButtons(false)
    }

    private fun displayButtons(display: Boolean) {
        light_revert_button.visibility = if (display) View.VISIBLE else View.INVISIBLE
        light_save_button.visibility = if (display) View.VISIBLE else View.INVISIBLE
    }

    private fun areButtonsDisplayed() = light_revert_button.visibility == View.VISIBLE
            && light_save_button.visibility == View.VISIBLE
}