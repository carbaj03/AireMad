package com.acv.airmad.ui.search


import com.acv.airmad.*
import com.acv.airmad.ui.StationViewModel
import com.acv.airmad.ui.common.BaseFragment
import com.acv.airmad.ui.common.Divider
import com.acv.airmad.ui.common.StationAdapter
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class ListFragment : BaseFragment() {
    private val stationAdapter
            by lazy { StationAdapter(holder = ::ListViewHolder) {} }
    private val model
            by lazy { viewModelProviders<StationViewModel>() }

    override fun getLayout() =
            R.layout.fragment_list

    override fun onCreate() {
        setupRecyclerView()

        launch(UI) {
            observe { model.getStations() } `do` { stationAdapter.add(it) }
        }
    }

    private fun setupRecyclerView() =
            with(rvStation) {
                layoutManager = linearLayoutManager()
                addItemDecoration(Divider(context.color(R.color.primary), 1f))
                adapter = stationAdapter
            }

}