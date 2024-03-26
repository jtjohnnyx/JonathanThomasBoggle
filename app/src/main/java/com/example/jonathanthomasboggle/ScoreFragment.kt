package com.example.jonathanthomasboggle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jonathanthomasboggle.databinding.FragmentScoreBinding


class ScoreFragment : Fragment() {

    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    private val BVM: BoggleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BVM.score.observe(viewLifecycleOwner, Observer {
            // Update the text of the textView with the current value of the number LiveData
            val score = it.toString()
            //binding.instructions.text = score
            Log.d("SBT2", score)
            binding.score.text = "SCORE: $score"
        })

        binding.newgame.setOnClickListener() {
            Log.d("GMC", BVM.getCount().toString())
            BVM.resetScore()
            BVM.incrGameCount()
            Log.d("GMC", BVM.getCount().toString())

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}