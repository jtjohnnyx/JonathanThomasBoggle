package com.example.jonathanthomasboggle

import android.content.Context
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class BoggleViewModel : ViewModel() {

    private val letters = mutableListOf(
        Letter(R.id.button0, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button1, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button2, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button3, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button4, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button5, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button6, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button7, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button8, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button9, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button10, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button11, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button12, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button13, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button14, '#', R.drawable.letter_default_enabled, false),
        Letter(R.id.button15, '#', R.drawable.letter_default_enabled, false),
    )

    private val words = mutableListOf<String>()

    fun addWord(word: String) {
        words.add(word)
    }

    fun resetWords() {
        words.clear()
    }

    private val _score = MutableLiveData<Int>()

    private val _gameCount = MutableLiveData<Int>()

    val score: LiveData<Int>
        get() = _score

    fun getScore() : Int? {
        return _score.value
    }

    val gameCount: LiveData<Int>
        get() = _gameCount

    init {
        _score.value = 0
        _gameCount.value = 0
    }

    fun getCount() : Int? {
        return _gameCount.value
    }

    fun updateScore(i : Int) {
        if (_score.value?.plus(i)!! < 0)
            _score.value = 0
        else
            _score.value = _score.value?.plus(i)
    }

    fun resetScore() {
        _score.value = 0
    }

    fun incrGameCount() {
        _gameCount.value = gameCount.value?.plus(1)
    }


    fun wordExists(word : String) : Boolean {
        return word in words
    }

    fun readFile(context: Context, fileName: String): List<String> {
        //val stringBuilder = StringBuilder()
        val ls = mutableListOf<String>()
        try {
            val inputStream: InputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                //stringBuilder.append(line).append('\n')
                ls.add(line.uppercase())
                line = reader.readLine()
            }
            reader.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // stringBuilder.toString()
        return ls
    }



    fun setLetter(i : Int, l : Char, @DrawableRes id : Int, c : Boolean) {
        letters[i].letter = l
        letters[i].imageId = id
        letters[i].chosen = c
    }

    fun getLetter(i : Int) : Letter {
        return letters[i]
    }

    fun disableLetter(i : Int) {

    }

}