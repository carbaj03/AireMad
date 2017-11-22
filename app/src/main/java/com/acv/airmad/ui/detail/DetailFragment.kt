package com.acv.airmad.ui.detail

import com.acv.airmad.R
import com.acv.airmad.configToolbar
import com.acv.airmad.ui.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : BaseFragment() {
    override fun getLayout() =
            R.layout.fragment_detail

    override fun onCreate() {
//        configToolbar("Android Developer")
        tvStation.setText("afdsfsf")
    }

}
