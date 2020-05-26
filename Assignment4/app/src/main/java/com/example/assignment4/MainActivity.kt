package com.example.assignment4

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var range = true  // 출제 범위 (All or Favorite)
    var testNum = 0 // 출제 문항 수
    var ansNum = 0 // 맞은 문항 수
    var words = ArrayList<MyData>() // 영단어와 뜻 저장
    var wordMap = mutableMapOf<Int, MyData>()
    var favorites = ArrayList<MyData>()
    var id = 0
    var ansWord = arrayOfNulls<MyData>(4)
    lateinit var adapter: MyAdapter
    lateinit var tts: TextToSpeech
    var isTtsReady = false
    var problemNum = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }
    override fun onStop() {
        super.onStop()
        tts.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    fun readFileScan(scan: Scanner){
        while(scan.hasNextLine()){
            val word = scan.nextLine()
            val meaning = scan.nextLine()
            val myData = MyData(id, word, meaning)
            words.add(myData)
            wordMap[id++] = myData
        }
        scan.close()
    }

    fun readFile(){
        val scan: Scanner = Scanner(resources.openRawResource(R.raw.words))
        //val scan: Scanner = Scanner(resources.openRawResource(R.raw.sample))    // 단어 추가 제대로 되었는지 테스트용
        readFileScan(scan)
        var mystream: FileInputStream?
        try {   // 단어 추가를 하지 않아 addwords.txt 파일이 없을 경우를 대비해서.
            mystream = openFileInput("addwords.txt")
        } catch(e: IOException){
            mystream = null
        }
        if(mystream != null) {
            val scan2 = Scanner(mystream)
            readFileScan(scan2)
        }
    }

    fun readfavoritefile() {
        var mystream: FileInputStream?
        try {   // 아직 즐겨찾기를 하지 않아 addwords.txt 파일이 없을 경우를 대비해서.
            mystream = openFileInput("favoriteIds.txt")
        } catch (e: IOException) {
            mystream = null
        }
        if (mystream != null) {
            val scan = Scanner(mystream)
            while (scan.hasNextLine()) {
                val num = scan.nextLine().toInt()
                // val meaning = scan.nextLine()
                if (wordMap[num] != null) {
                    favorites.add(wordMap[num]!!)
                }
            }
            scan.close()
        }
    }

    private fun init(){
        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            isTtsReady = true
            tts.language = Locale.US
        })

        // QuizOption Activity에서 온 inten 값들 전달받기
        var bundle :Bundle ? = intent.extras
        range = bundle!!.getBoolean("range")
        testNum = bundle!!.getInt("testNum")

        readFile()
        readfavoritefile()

        answerList.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)

        startTest()
    }

    fun endTest() {
        // test 끝난 후 -> intent로 값 전달하고 화면 전환
        val i = Intent(this, QuizScoreActivity::class.java)
        i.putExtra("ansNum", ansNum)
        i.putExtra("testNum", testNum)
        startActivity(i)
    }

    fun startTest(){
        bg.setBackgroundColor(Color.rgb(255,253,252))
        answerList.setBackgroundColor(Color.rgb(255,253,252))
        imageView.setImageResource(R.drawable.ing)
        questionNum.text = problemNum.toString()

        if(range) {
            for (i in ansWord.indices) {
                var rIdx = (0..(words.size - 1)).random()
                ansWord[i] =
                    MyData(-1, words[rIdx].word, words[rIdx].meaning)  // ansWord에 랜덤으로 뽑은 단어 넣기
                //ansWord[i] = array[(0..array.size).random()]
                //ansMean[i] = words[ansWord[i]]  // ansMean에는 뜻이 들어감
            }
        }
        else {  // favorite
            for (i in ansWord.indices) {
                var rIdx = (0..(favorites.size - 1)).random()
                ansWord[i] =
                    MyData(-1, favorites[rIdx].word, favorites[rIdx].meaning)  // ansWord에 랜덤으로 뽑은 단어 넣기
            }
        }

        val ansIdx = (0..3).random()   // 정답이 될 단어의 인덱스 정하기

        adapter = MyAdapter(ansWord)

        word.text = ansWord[ansIdx]?.word // 정답이 될 영단어를 띄움
        adapter.ansIdxAdapter = ansIdx  // adapter에 정답이 될 index 전달

        adapter.itemClickListener = object :MyAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: MyAdapter.MyViewHolder,
                view: View,
                data: MyData?,
                position: Int
            ) { // 클릭 이벤트 처리
                if(position == ansIdx){ // 정답
                    bg.setBackgroundColor(Color.rgb(169,225,196))
                    answerList.setBackgroundColor(Color.rgb(169,225,196))
                    imageView.setImageResource(R.drawable.right)
                    ansNum++
                }
                else{   // 오답
                    bg.setBackgroundColor(Color.rgb(227,190,197))
                    answerList.setBackgroundColor(Color.rgb(227,190,197))
                    imageView.setImageResource(R.drawable.wrong)
                }

                if(isTtsReady)
                    tts.speak(ansWord[ansIdx]?.word, TextToSpeech.QUEUE_ADD, null, null)

                problemNum++
                if(problemNum <= testNum)
                    Handler().postDelayed(::startTest, 2000)
                else
                    Handler().postDelayed(::endTest, 2000)
            }
        }
        answerList.adapter = adapter    // 어댑터 달기
    }
}
