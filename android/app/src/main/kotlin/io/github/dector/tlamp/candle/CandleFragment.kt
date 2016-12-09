package io.github.dector.tlamp.candle

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.dector.tlamp.R

class CandleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater?.inflate(R.layout.fragment_candle, container, false)
}