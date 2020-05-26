package com.example.assignment4

import android.graphics.Color
import android.util.SparseBooleanArray
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.assignment4.AllVocFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_all_voc.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private val mValues: ArrayList<MyData>,
    private val mIds: ArrayList<Int>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    //private val mOnClickListener: View.OnClickListener
    var isSelected = SparseBooleanArray()
    var isFavorite = SparseBooleanArray()
    var str = ""    // 검색으로 들어올 단어
    var items = ArrayList<MyData>()  // 검색 결과 담아둠

    /*init {
        mOnClickListener = View.OnClickListener { v ->  // row의 view 말하는듯?
            val itemIdx = v.tag as Int  // adapterPosition 대신 tag로 위치 받아옴
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.

            if(isSelected.get(itemIdx)) {   // 이미 뜻이 보이는 경우
                isSelected.delete(itemIdx)
            }
            else    // 뜻이 보이지 않는 경우
                isSelected.put(itemIdx, true)
            notifyItemChanged(itemIdx)
            mListener?.onListFragmentInteraction(mValues[itemIdx], itemIdx)
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_all_voc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (str == "" || position < items.size) {    // IndexOutOfException 방지.. 근데 너무 스파게티 코드다...

            lateinit var item: MyData
            if (str == "")
                item = mValues[position]
            else
                item = items[position]

            //val itemIdx = position
            holder.wordView.text = item.word

            if (mValues[position].id in mIds) {
                isFavorite.put(position, true)
            }
            else {
                if(isFavorite.get(position))
                    isFavorite.delete(position)
            }

            if (isFavorite.get(position))
                holder.favoriteBtn.setImageResource(R.drawable.ic_star_pink)
            else
                holder.favoriteBtn.setImageResource(R.drawable.ic_star_white)
            if (isSelected.get(position)) {  // 뜻 보이는 리스트에 있는 경우
                holder.meaningView.text = item.meaning
                holder.meaningView.visibility = View.VISIBLE
            } else    // 없는 경우
                holder.meaningView.visibility = View.GONE

        }

        /*with(holder.mView) {
            // tag = item
            //tag = itemIdx   // adapterPosition 대신 이렇게 위치 전달함
            setOnClickListener(mOnClickListener)
        }*/
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val wordView :TextView = mView.wordText
        val favoriteBtn :ImageView = mView.favoriteBtn
        val meaningView: TextView = mView.meaningText

        init {
            wordView.setOnClickListener {
                if(isSelected.get(adapterPosition)) {   // 이미 뜻이 보이는 경우
                    isSelected.delete(adapterPosition)  // 목록에서 빼기
                }
                else    // 뜻이 보이지 않는 경우
                    isSelected.put(adapterPosition, true)   // 목록에서 넣기
                notifyItemChanged(adapterPosition)
                mListener?.onListFragmentInteraction(mValues[adapterPosition], adapterPosition)
            }
            favoriteBtn.setOnClickListener {
                if(!isFavorite.get(adapterPosition)) {  // 즐겨찾기에 추가되지 않은 경우
                    isFavorite.put(adapterPosition, true)
                    mListener?.onClickFavoriteBtn(mValues[adapterPosition], true)
                }
                else {  // 이미 즐겨찾기에 저장된 경우
                    isFavorite.delete(adapterPosition)
                    mListener?.onClickFavoriteBtn(mValues[adapterPosition], false)
                }
                notifyItemChanged(adapterPosition)
            }

        }

        override fun toString(): String {
            return super.toString() + " '" + wordView.text + "'"
        }
    }

    fun moveItem(oldPos: Int, newPos: Int){
        val item = mValues[oldPos]
        mValues.removeAt(oldPos)
        isSelected.delete(oldPos)
        isFavorite.delete(oldPos)
        mValues.add(newPos, item)
        isSelected.put(newPos, true)
        isFavorite.put(newPos, true)
        notifyItemMoved(oldPos, newPos) // data가 바뀌었어. 알려줌
    }

    fun searchStarted() {
        if(str != "") {
            items.clear()
            for(data in mValues)
                if(data.word.startsWith(str))
                    items.add(data)
        }
        notifyDataSetChanged()
    }
}
