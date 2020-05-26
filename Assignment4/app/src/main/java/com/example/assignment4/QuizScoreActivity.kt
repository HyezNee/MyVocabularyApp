package com.example.assignment4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_quiz_score.*

class QuizScoreActivity : AppCompatActivity() {
    var ansNum = 0
    var testNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_score)
        init()
    }

    fun init() {
        var bundle :Bundle ? = intent.extras
        ansNum = bundle!!.getInt("ansNum")
        testNum = bundle!!.getInt("testNum")

        val ansStr = ansNum.toString() + " / " + testNum.toString()
        scoreText.text = ansStr

        mainBtn.setOnClickListener {
            val i = Intent(this, IntroActivity::class.java)
            startActivity(i)
        }
    }
}
