package io.github.dector.tlamp.gradient

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import io.github.dector.tlamp.R
import io.github.dector.tlamp.connection.ILampDataLoader
import kotlinx.android.synthetic.main.fragment_gradient.*

class GradientFragment(private val dataLoader: ILampDataLoader) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater?.inflate(R.layout.fragment_gradient, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        gradient_activate_button.setOnClickListener {
            dataLoader.activateGradient()
        }

        gradient_period_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                updatePeriodTitle(value)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}

            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        updatePeriodTitle(gradient_period_seek_bar.progress)
    }

    private fun updatePeriodTitle(value: Int) {
        gradient_period_title.text = getString(R.string.gradient_period_title_format,
                formatTime(progressToSeconds(value)))
    }

    private fun progressToSeconds(value: Int) = (value + 1) * 10

    private fun formatTime(seconds: Int): String {
        val minutesValue = seconds / 60
        val secondsValue = seconds % 60

        return if (minutesValue > 0) {
            if (secondsValue > 0) {
                getString(R.string.format_minutes_seconds, minutesValue, secondsValue)
            } else {
                getString(R.string.format_minutes, minutesValue)
            }
        } else {
            getString(R.string.format_seconds, secondsValue)
        }
    }
}