package com.example.dodgerabbit

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class GameOver : AppCompatActivity() {

    private lateinit var tvPoints: TextView
    private lateinit var tvHighest: TextView
    private lateinit var ivNewHighest: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        // Initialize views using findViewById
        tvPoints = findViewById(R.id.tvPoints)
        tvHighest = findViewById(R.id.tvHighest)
        ivNewHighest = findViewById(R.id.ivNewHighest)

        // Retrieve points from intent
        val points = intent.getIntExtra("points", 0)

        // Display the points
        tvPoints.text = points.toString()

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("my_pref", MODE_PRIVATE)

        // Retrieve the highest score
        val highest = sharedPreferences.getInt("highest", 0)

        // Check for a new highest score
        if (points > highest) {
            ivNewHighest.visibility = View.VISIBLE
            sharedPreferences.edit {
                putInt("highest", points)
                apply()
            }
            tvHighest.text = points.toString()
        } else {
            tvHighest.text = highest.toString()
        }
    }

    fun restart(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(view: View) {
        finish()
    }
}
