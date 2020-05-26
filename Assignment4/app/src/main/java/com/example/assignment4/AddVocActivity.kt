package com.example.assignment4

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_voc.*
import kotlinx.android.synthetic.main.activity_intro.*
import java.io.PrintStream

class AddVocActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_voc)
        init()
    }

    private fun init() {
        addBtn2.setOnClickListener {
            var word = editWord.text.toString()
            var meaning = editMeaning.text.toString()
            writeFile(word, meaning)
        }

        cancelBtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun writeFile(word: String, meaning: String) {
        // 단어 추가하여 파일에 쓰기
        val output = PrintStream(openFileOutput("addwords.txt", Context.MODE_APPEND))
        output.println(word)
        output.println(meaning)
        output.close()

        // 인텐트로 값 전달하기
        val i = Intent()    // 상대족에서 한 번 호출하면 여긴 클래스 명시 안해줘도 되나봄
        i.putExtra("voc", MyData(-1, word, meaning))
        setResult(Activity.RESULT_OK, i)    // setresult로 resultCode에 값 전달
        finish()
    }
}
