package com.example.assignment4

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MyAdapter(val items: Array<MyData?>)
    : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    interface OnItemClickListener{
        fun OnItemClick(holder: MyViewHolder, view: View, data: MyData?, position: Int) // 세 번째 인자는 굳이 안 넣어도 될 것 같기도...
    }

    var itemClickListener: OnItemClickListener ?= null
    var selected = -1;  // selected position
    var ansIdxAdapter = -1

    inner class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var meaningView: TextView = itemView.findViewById(R.id.ansList)
        init{
            itemView.setOnClickListener{
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
                selected = adapterPosition
                notifyItemRangeChanged(0, 4)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        holder.meaningView.text = items[position]?.meaning
        // 뷰를 스크롤 했을 때 엉뚱한 위치에 이벤트가 일어나지 않게 하려면 bind할 때 이벤트 처리를 해야 함
        if (selected != -1) {    // 선택했을 때
            if (position == selected) {   // 선택된 보기일 경우
                if (position == ansIdxAdapter)  // 정답
                    holder.meaningView.setBackgroundColor(Color.rgb(131, 211, 126))
                else    // 오답
                    holder.meaningView.setBackgroundColor(Color.rgb(196, 161, 237))
            }
            else {  // 선택되지 않은 보기일 경우
                if (position == ansIdxAdapter)  // 오답일 때 정답 정답 선택지 표시
                    holder.meaningView.setBackgroundColor(Color.rgb(131, 211, 126))
                else    // 나머지 보기는 회색 처리
                    holder.meaningView.setBackgroundColor(Color.rgb(211, 211, 211))
            }
        }
    }

}