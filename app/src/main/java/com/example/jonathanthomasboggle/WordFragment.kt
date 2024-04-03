package com.example.jonathanthomasboggle


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.jonathanthomasboggle.databinding.FragmentWordBinding
import kotlin.math.floor
import kotlin.random.Random

class WordFragment : Fragment() {

    private var _binding: FragmentWordBinding? = null
    private val binding get() = _binding!!

    private lateinit var buttons : List<ImageButton>

    private val selectedButtons = HashSet<ImageButton>()

    private var last = -1

    private var connectors = mutableListOf<Int>()

    private lateinit var dict : List<String>

    private val BVM: BoggleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BVM.score.observe(viewLifecycleOwner, Observer {
            // Update the text of the textView with the current value of the number LiveData
            val score = it.toString()
            //binding.instructions.text = score
            Log.d("SBT", score)
        })

        BVM.gameCount.observe(viewLifecycleOwner, Observer {
            //val gameCount = it.toString()
            Log.d("SBT", "new game")
            clearLetters()
            BVM.resetWords()
            assignLetters("Scrabble")
        })

        buttons = listOf(binding.button0, binding.button1,binding.button2, binding.button3,
            binding.button4, binding.button5,binding.button6, binding.button7,
            binding.button8, binding.button9,binding.button10, binding.button11,
            binding.button12, binding.button13,binding.button14, binding.button15)

        dict = BVM.readFile(requireContext(),"words.txt")

        //assignLetters("Scrabble")

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                var letter = BVM.getLetter(index).letter
                var imageId = resources.getIdentifier("letter_${letter}_selected", "drawable", requireContext().packageName)
                BVM.setLetter(index, letter, imageId, true)
                button.setImageResource(imageId)
                button.isEnabled = false
                updateLetters(index)
                binding.word.append(letter.uppercaseChar().toString())

                if (last != -1) {
                    drawConnector(last, index, getDirection(last, index))
                    letter = BVM.getLetter(last).letter
                    imageId = resources.getIdentifier("letter_${letter}_disabled", "drawable", requireContext().packageName)
                    BVM.setLetter(last, letter, imageId, true)
                    buttons[last].setImageResource(imageId)
                }
                last = index
            }
        }

        binding.clear.setOnClickListener() {
            clearLetters()
        }

        binding.submit.setOnClickListener() {
            val word = binding.word.text.toString()
            if (!binding.word.text.isNullOrEmpty()) {
                if (validWord(word)) {
                    //binding.instructions.text = "Correct"
                    BVM.addWord(word)
                    BVM.updateScore(calcScore(word))

                    //binding.word.text = BVM.getScore().toString()
                }
                else {
                    //binding.instructions.text = "Invalid"
                    //Log.d("SBT", BVM.getScore().toString())
                    BVM.updateScore(-10)
                    //Log.d("SBT", BVM.getScore().toString())
                    //binding.word.text = BVM.getScore().toString()
                }
                clearLetters()
            } else
                Log.d("VWD", "no text")

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        //BVM.score.removeObserver(Observer {})
        _binding = null
    }

    private fun dpToPx(dp: Float): Float {
        val scale = requireContext().resources.displayMetrics.density
        return dp * scale
    }

    private fun getDirection(last : Int, current : Int) : String {

        val start = minOf(last, current)
        val end = maxOf(last, current)
        if (end - start == 1)
            return "Horizontal"
        else if (end % 4 == start % 4)
            return "Vertical"
        else if (end - start == 5)
            return "LDiagonal"
        return "RDiagonal"
    }

    private fun drawConnector(last : Int, current : Int, direction : String) {

        val connView = ConnectorView(requireContext(), null)
        connView.id = View.generateViewId()
        connectors.add(connView.id)

        // Set custom attributes
        val start = minOf(last, current)
        val end = maxOf(last, current)
        val width = buttons[start].width.toFloat()
        val height = buttons[start].height.toFloat()
        val left = binding.gridLayout.left.toFloat()
        val top = binding.gridLayout.top.toFloat()
        val padding = buttons[start].paddingStart.toFloat()
        val frame = ((height - (2 * padding)) * 0.04f)

        if (direction == "Vertical") {
            connView.startX = left + (width / 2) + ((start % 4) * width)
            connView.startY = top - padding + ((floor(start / 4.0).toInt() + 1) * height) - frame
            connView.endX = left + (width / 2) + ((end % 4) * width)
            connView.endY = top + padding + (floor(end / 4.0).toInt() * height) + frame
        }
        if (direction == "Horizontal") {
            connView.startX = left - padding + (((start % 4) + 1) * width) - frame
            connView.startY = top + (height / 2) + (floor(start / 4.0).toInt() * height)
            connView.endX = left + padding + ((end % 4) * width) + frame
            connView.endY = top + (height / 2) + (floor(end / 4.0).toInt() * height)
            //Log.d("HZT", "startX:${connView.startX} startY:${connView.startY} endX:${connView.endX} endY:${connView.endY}")
        }
        if (direction == "LDiagonal") {
            connView.startX = left - padding + (((start % 4) + 1) * width) - frame
            connView.startY = top - padding + ((floor(start / 4.0).toInt() + 1) * height) - frame
            connView.endX = left + padding + (((start % 4) + 1) * width) + frame
            connView.endY = top + padding + ((floor(start / 4.0).toInt() + 1) * height) + frame
        }
        if (direction == "RDiagonal") {
            connView.startX = left + padding + ((start % 4) * width) + frame
            connView.startY = top + height - padding + (floor(start / 4.0).toInt() * height) - frame
            connView.endX = left - padding + ((start % 4) * width) - frame
            connView.endY = top + height + padding + (floor(start / 4.0).toInt() * height) + frame
            Log.d("HZT", "startX:${connView.startX} startY:${connView.startY} endX:${connView.endX} endY:${connView.endY}")
        }

        connView.width = 0.08f * width

        // Invalidate the LineView to trigger redraw
        connView.invalidate()

        binding.conslayout.addView(connView)
    }

    private fun calcScore(word: String) : Int {
        val numVowels = countVowels(word)
        val numCons = word.length - numVowels
        return if (containsSpecial(word))
            2 * (numCons + (5 * numVowels))
        else
            (numCons + (5 * numVowels))
    }

    private fun containsSpecial(word: String) : Boolean {
        val charactersToCheck = setOf('S', 'Z', 'P', 'X', 'Q')
        return word.any { it in charactersToCheck }
    }

    private fun validWord(word: String) : Boolean {
        //Log.d("VWD", dict.toString())
        var valid = true
        if (BVM.wordExists(word)) {
            valid = false
            Toast.makeText(requireContext(), "Word already guessed (-10 pts)", Toast.LENGTH_SHORT).show()
        } else if (countVowels(word) < 2) {
            valid = false
            Toast.makeText(requireContext(), "Not enough vowels (-10 pts)", Toast.LENGTH_SHORT).show()
        } else if (word.length < 4) {
            valid = false
            Toast.makeText(requireContext(), "Word too short (-10 pts)", Toast.LENGTH_SHORT).show()
        } else if (word !in dict) {
            valid = false
            Toast.makeText(requireContext(), "Not a valid word (-10 pts)", Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(requireContext(), "Correct! (+${calcScore(word)} pts)", Toast.LENGTH_SHORT).show()
        return valid
    }

    private fun countVowels(word: String) : Int {
        val vowels = setOf('A', 'E', 'I', 'O', 'U')
        var count = 0
        for (char in word) {
            if (char in vowels) {
                count++
            }
        }
        return count
    }

    private fun clearLetters() {
        for (i in 0..15) {
            val l = BVM.getLetter(i)
            val imageId = resources.getIdentifier("letter_${l.letter}_valid", "drawable", requireContext().packageName)
            BVM.setLetter(i, l.letter, imageId, false)
            buttons[i].setImageResource(imageId)
            buttons[i].isEnabled = true
//            testing
//            binding.instructions.text = ""
        }
        for (id in connectors) {
            val conn = binding.conslayout.findViewById<ConnectorView>(id)
            binding.conslayout.removeView(conn)
        }
        binding.word.text = ""
        connectors.clear()
        last = -1
    }

    private fun updateLetters(i : Int) {
        val ls = getAdj(i)
        Log.d("ADJ", ls.toString())
        for (j in 0..15) {
            var l = BVM.getLetter(j)
            if (l.chosen) {
                Log.d("ADJ", l.letter.toString() + "(" + j.toString() + "):" + "chosen")
                continue
            }
            if (j in ls) {
                val imageId = resources.getIdentifier("letter_${l.letter}_valid", "drawable", requireContext().packageName)
                BVM.setLetter(j, l.letter, imageId, false)
//                binding.root.findViewById<ImageButton>(l.letterId).setImageResource(imageId)
//                binding.root.findViewById<ImageButton>(l.letterId).isEnabled = true
                buttons[j].setImageResource(imageId)
                buttons[j].isEnabled = true
                Log.d("ADJ", l.letter.toString() + "(" + j.toString() + "):" + "in ls")

            } else {
                val imageId = resources.getIdentifier("letter_${l.letter}_enabled", "drawable", requireContext().packageName)
                BVM.setLetter(j, l.letter, imageId, false)
//                binding.root.findViewById<ImageButton>(l.letterId).setImageResource(imageId)
//                binding.root.findViewById<ImageButton>(l.letterId).isEnabled = false
                buttons[j].setImageResource(imageId)
                buttons[j].isEnabled = false
                Log.d("ADJ", l.letter.toString() + "(" + j.toString() + "):" + "not in ls")
            }
        }
    }

    private fun getAdj(i : Int) : List<Int> {
        val ls = mutableListOf<Int>() //topbottom
        if (i - 4 >= 0)
            ls.add(i - 4)
        if (i + 4 <= 15)
            ls.add(i + 4)
        val q = i / 4 //leftright
        if ((i - 1) / 4 == q && i != 0)
            ls.add(i - 1)
        if ((i + 1) / 4 == q && i != 15)
            ls.add(i + 1)
        if ((i - 5) / 4 == (q - 1) && i > 4) //diags
            ls.add(i - 5)
        if ((i - 3) / 4 == (q - 1) && i > 2)
            ls.add(i - 3)
        if ((i + 3) / 4 == (q + 1) && i < 13)
            ls.add(i + 3)
        if ((i + 5) / 4 == (q + 1) && i < 11)
            ls.add(i + 5)

        return ls
    }

    private fun assignLetters(mode : String) {
        for (i in 0..15) {

            var letter = '#'
            if (mode == "Random") { //equal 1/26 probs
                letter = (Random.nextInt(26) + 'a'.code).toChar()
            }

            if (mode == "Boggle") { //boggle value assoc. probs
                val randomNumber = Random.nextDouble() // Generate a random number between 0 and 1
                letter = when {
                    randomNumber < 0.25 -> pickLetter("aeiou".toList())
                    randomNumber < 0.35 -> pickLetter("pqsxz".toList())
                    else -> pickLetter("bcdfghjklmnrtvwy".toList())
                }
            }

            if (mode == "Scrabble") { //scrabble probs
                val randomNumber = Random.nextDouble() // Generate a random number between 0 and 1
                letter = when {
                    randomNumber < 0.12 -> 'e'
                    randomNumber < 0.30 -> pickLetter("ai".toList())
                    randomNumber < 0.38 -> 'o'
                    randomNumber < 0.62 -> pickLetter("nrts".toList())
                    randomNumber < 0.74 -> pickLetter("lud".toList())
                    randomNumber < 0.77 -> 'g'
                    randomNumber < 0.95 -> pickLetter("bcmpfhvwy".toList())
                    else -> pickLetter("kjzxq".toList())
                }
            }

            //val buttonId = resources.getIdentifier("button${i}", "id", requireContext().packageName)
            val imageId = resources.getIdentifier("letter_${letter}_valid", "drawable", requireContext().packageName)

            //binding.root.findViewById<ImageButton>(buttonId).setImageResource(imageId)
            BVM.setLetter(i, letter, imageId, false)
//            binding.root.findViewById<ImageButton>(BVM.getLetter(i).letterId).setImageResource(imageId)
            buttons[i].setImageResource(imageId)
        }
    }

    private fun pickLetter(chars: List<Char>) : Char {
        val randomIndex = (chars.indices).random()
        return chars[randomIndex]
    }
    

}
