package com.example.jonathanthomasboggle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.jonathanthomasboggle.databinding.FragmentScoreBinding
import java.util.Objects
import kotlin.math.sqrt


class ScoreFragment : Fragment() {

    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    private val BVM: BoggleViewModel by activityViewModels()

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f


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

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!
            .registerListener(sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        BVM.score.observe(viewLifecycleOwner, Observer {
            // Update the text of the textView with the current value of the number LiveData
            val score = it.toString()
            //binding.instructions.text = score
            Log.d("SBT2", score)
            binding.score.text = "SCORE: $score"
        })

        binding.newgame.setOnClickListener() {
            Log.d("GMC", BVM.getCount().toString())
            Toast.makeText(requireContext(), "New Game!", Toast.LENGTH_SHORT).show()
            BVM.resetScore()
            BVM.incrGameCount()
            Log.d("GMC", BVM.getCount().toString())

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            if (acceleration > 9) {
                Log.d("GMC", BVM.getCount().toString())
                BVM.resetScore()
                BVM.incrGameCount()
                Toast.makeText(requireContext(), "New Game!", Toast.LENGTH_SHORT).show()
                Log.d("GMC", BVM.getCount().toString())
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
}