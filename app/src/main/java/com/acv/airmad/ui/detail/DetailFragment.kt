package com.acv.airmad.ui.detail

import com.acv.airmad.R
import com.acv.airmad.ui.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : BaseFragment() {
    override fun getLayout() =
            R.layout.fragment_detail

    override fun onCreate() {

        with(tabLayout) {
            tabGravity = android.support.design.widget.TabLayout.GRAVITY_FILL
            addTab(newTab().setText("REFERENTES"))
            addTab(newTab().setText("INTERESES"))
            addTab(newTab().setText("OBJETIVOS"))
        }
    }

}
