package com.pamsimas.pamsimasapps.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import com.pamsimas.pamsimasapps.databinding.ActivityOnboardingBinding
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.login.LoginActivity
import java.text.SimpleDateFormat
import java.util.*

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@OnboardingActivity, LoginActivity::class.java)
            startActivity(intent)
            //showDataRangePicker()
        }
    }

    private fun showDataRangePicker() {

        val dateRangePicker =
            MaterialDatePicker
                .Builder.dateRangePicker()
                .setTitleText("Select Date")
                .build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { dateSelected ->

            val startDate = dateSelected.first
            val endDate = dateSelected.second
            //calculate differ
            val diff: Long = endDate - startDate
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            Toast.makeText(this, "$days", Toast.LENGTH_SHORT).show()

            if (startDate != null && endDate != null) {
                Toast.makeText(this, "StartDate: ${convertLongToTime(startDate)}\n" +
                        "EndDate: ${convertLongToTime(endDate)}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault())
        return format.format(date)
    }
}