package com.example.assignment4

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    interface FragmentChangeListener {
        fun makeFragment(position: Int) :Fragment
    }
    var listener :FragmentChangeListener? = null

    override fun getItemCount(): Int {
        //TODO("Not yet implemented")
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        //TODO("Not yet implemented")
        return when (position){
            0 -> listener!!.makeFragment(0)   // 인자로 들어갈 ArrayList가 Activity class에 있으므로
            1 -> listener!!.makeFragment(1)   // 인터페이스를 구현해서 넘길 수 밖에 없었음...
            else -> listener!!.makeFragment(0)
        }
    }

}