package com.example.assignment4

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_word_list.*
import java.io.FileInputStream
import java.io.IOException
import java.io.PrintStream
import java.util.*

class WordListActivity : AppCompatActivity(), AllVocFragment.OnListFragmentInteractionListener {

    lateinit var tts: TextToSpeech
    var isTtsReady = false
    var words = ArrayList<MyData>() // 영단어와 뜻 저장 -> fragment에 전달 예정
    val favorites = ArrayList<MyData>() // 즐겨찾기된 단어 정보 저장
    val favoritesIds = ArrayList<Int>() // 즐겨찾기된 ID 저장 -> 그대로 fragment의 recyclerviewadapter에 넘길 것임
    val wordMap = mutableMapOf<Int, MyData>()   // ID 와 MyData를 연결해줌
    val textArray = arrayListOf<String>("ALL", "FAVORITE")
    lateinit var stateAdapter: MyFragStateAdapter
    var fragments = mutableListOf<AllVocFragment?>(null, null)
    var position = 0    // 현재 fragment 위치

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)
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

    fun init() {
        // tts 설정
        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            isTtsReady = true
            tts.language = Locale.ENGLISH
        })
        readfile()  // 파일 읽어들이기
        readfavoritefile()  // 즐겨찾기 읽어들이기

        // fragment 달기 -> 탭 달아서 안해줘도됨~
        /*val fragment = supportFragmentManager.beginTransaction()
        val allVocFragment = AllVocFragment.newAllVocFragment(words)
        fragment.replace(R.id.frameLayout, allVocFragment)
        fragment.commit()*/

        stateAdapter = MyFragStateAdapter(this)
        stateAdapter.listener = object : MyFragStateAdapter.FragmentChangeListener {
            override fun makeFragment(position: Int): Fragment {
                fragments[0] = AllVocFragment.newAllVocFragment(words, favoritesIds)
                fragments[1] = AllVocFragment.newAllVocFragment(favorites, favoritesIds)
                this@WordListActivity.position = position
                return fragments[position]!!
                /*when (position) {
                    0 -> return allFragment
                    1 -> return favFragment
                    else -> return allFragment  // 굳이 안해줘도 되지만..
                }*/
            }
        }

        // 탭 달기
        viewPager.adapter = stateAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = textArray[position]
            // 이렇게 하면 tab에 textArray 정보가 들어감
        }.attach()
    }

    // interface method overriding
    override fun onListFragmentInteraction(item: MyData?, position: Int) {
        if (isTtsReady)
            tts.speak(item?.word, TextToSpeech.QUEUE_ADD, null, null)
    }

    override fun onClickFavoriteBtn(
        item: MyData?,
        flag: Boolean   // true : 추가, false : 삭제
    ) {    // 즐겨찾기 단어들 갱신해서 파일에 다시 써주는 역할
        var deleteIdx = -1
        if (flag) {
            favorites.add(item!!)
            favoritesIds.add(item.id)
        }
        for (i in favorites.indices) {
            if (!flag && favorites[i].id == item?.id) { // 즐겨찾기 삭제
                deleteIdx = i
                break
            }
        }
        if (!flag) {
            favorites.removeAt(deleteIdx)
            favoritesIds.removeAt(deleteIdx)
        }
        writeFile(favoritesIds)
        fragments[position]?.myAdapter?.notifyDataSetChanged()
    }

    fun readFileScan(scan: Scanner) {
        var id = 0
        while (scan.hasNextLine()) {
            val word = scan.nextLine()
            val meaning = scan.nextLine()
            val myData = MyData(id, word, meaning)
            words.add(myData)
            wordMap[id++] = myData
        }
        scan.close()
    }

    fun readfile() {
        val scan: Scanner = Scanner(resources.openRawResource(R.raw.words))
        readFileScan(scan)
        var mystream: FileInputStream?
        try {   // 단어 추가를 하지 않아 addwords.txt 파일이 없을 경우를 대비해서.
            mystream = openFileInput("addwords.txt")
        } catch (e: IOException) {
            mystream = null
        }
        if (mystream != null) {
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
                    favoritesIds.add(num)
                    favorites.add(wordMap[num]!!)
                }
            }
            scan.close()
        }
    }

    fun writeFile(ids: ArrayList<Int>) {
        // favorite 지정된 단어 파일에 쓰기. 갱신
        val output = PrintStream(openFileOutput("favoriteIds.txt", Context.MODE_PRIVATE))
        for (data in ids)
            output.println(data)
        output.close()
    }

}
