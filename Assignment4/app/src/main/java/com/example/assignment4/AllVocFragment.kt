package com.example.assignment4

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.android.synthetic.main.activity_word_list.*
import java.util.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [AllVocFragment.OnListFragmentInteractionListener] interface.
 */
class AllVocFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null

    lateinit var myAdapter :MyItemRecyclerViewAdapter
    var words = ArrayList<MyData>() // 영단어와 뜻 저장
    var favoritesIDs = ArrayList<Int>()

    /*interface ReadFileOnActivity {
        fun readfile()
    }*/
    //var readFileOnActivity :ReadFileOnActivity? = null

    companion object {   // static 함수 -> 인자를 받아 fragment 생성 위해
        fun newAllVocFragment(words :ArrayList<MyData>, favoriteIDs :ArrayList<Int>) :AllVocFragment {
            val allVocFragment = AllVocFragment()
            allVocFragment.words = words
            allVocFragment.favoritesIDs = favoriteIDs
            return allVocFragment
        }
    }

    // itemtouchHelper : 각 항목 위치 이동
    val simpleCallback = object :
        ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            myAdapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
            return true;
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }   // 아래 : swipe 모션 자체가 일어나지 않도록 함
        override fun getSwipeDirs (recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            if (viewHolder is MyItemRecyclerViewAdapter.ViewHolder) return 0
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_voc_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                // adapter = MyItemRecyclerViewAdapter(words, listener)
                myAdapter = MyItemRecyclerViewAdapter(words, favoritesIDs, listener)
                adapter = myAdapter
            }

            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(view)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.searchWord?.addTextChangedListener(object :TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var str = s.toString()
                myAdapter.str = str
                myAdapter.searchStarted()
                //myAdapter.notifyDataSetChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onResume() {
        super.onResume()
        //myAdapter.isFavoriteIds = favoritesIDs
        myAdapter.notifyDataSetChanged()    // 하......... 이거때문이었구나........... 진짜 이것때문에
    }                                     // 삽질만 세시간을 하고 진자...........

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: MyData?, position: Int)
        fun onClickFavoriteBtn(item: MyData?, flag: Boolean)    // flag : 즐겨찾기 추가? 삭제?
    }
}
