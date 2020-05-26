package com.example.assignment4

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    val ADDVOC_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        init()
    }

    private fun init(){
        startBtn.setOnClickListener {
            // val i = Intent(this, MainActivity::class.java)
            val i = Intent(this, QuizOptionActivity::class.java)
            startActivity(i)
        }

        addBtn.setOnClickListener {
            val i = Intent(this, AddVocActivity::class.java)
            startActivityForResult(i, ADDVOC_REQUEST)   // 메세지 전달 받기
        }

        listBtn.setOnClickListener {
            val i = Intent(this, WordListActivity::class.java)
            startActivity(i)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ADDVOC_REQUEST ->{
                if(resultCode == Activity.RESULT_OK){
                    val rData = data?.getSerializableExtra("voc") as MyData
                    Toast.makeText(this, "[Add] " + rData.word + " : " + rData.meaning, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
