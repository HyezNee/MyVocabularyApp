package com.example.assignment4

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_quiz_option.*

class QuizOptionActivity : AppCompatActivity() {
    var range = true    // 퀴즈 출제 범위. true : all, false : 즐겨찾기
    var testNum = 0 // 퀴즈 문항 수
    lateinit var items :Array<String>
    lateinit var spinnerAdapter :ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_option)
        init()
    }

    fun init() {
        whereto.setOnClickListener {
            if(range) {   // ALL -> FAVORITE
                whereto.setText("FAVORITE")
                whereto.setBackgroundColor(Color.rgb(248, 203, 188))
                range = false
            }
            else {  // FAVORITE -> ALL
                whereto.setText("ALL")
                whereto.setBackgroundColor(Color.rgb(255, 234, 182))
                range = true
            }
        }

        items = resources.getStringArray(R.array.array_nums)
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                testNum = 5
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                testNum = items[position].toInt()
            }
        }

        quizStartBtn.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("range", range)
            i.putExtra("testNum", testNum)
            startActivity(i)
        }
    }
}
