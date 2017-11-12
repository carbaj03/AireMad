package com.acv.airmad


import kotlinx.android.synthetic.main.fragment_list.*


class ListFragment : BaseFragment() {
    private val skillAdapter
            by lazy { SkillAdapter(holder = ::ListViewHolder) {} }

    override fun getLayout() =
            R.layout.fragment_list

    override fun onCreate() {

        setupRecyclerView()
    }

    private fun setupRecyclerView() =
            with(rvSkills) {
                layoutManager = linearLayoutManager()
                addItemDecoration(Divider(context.color(R.color.primary), 1f))
                adapter = skillAdapter
            }

}