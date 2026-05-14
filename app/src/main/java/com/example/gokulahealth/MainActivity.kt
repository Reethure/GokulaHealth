package com.example.gokulahealth

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import java.util.Calendar

class MainActivity : AppCompatActivity() {

    // RESULT DATA

    private var cowData = ""
    private var milkData = ""
    private var vaccineData = ""

    // MILK HISTORY

    private val milkHistory = mutableListOf<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // SHARED PREFERENCES

        val sharedPreferences =
            getSharedPreferences(
                "GokulaHealthData",
                Context.MODE_PRIVATE
            )

        val editor =
            sharedPreferences.edit()

        // INPUTS

        val cowName =
            findViewById<EditText>(R.id.cowName)

        val cowId =
            findViewById<EditText>(R.id.cowId)

        val morningMilk =
            findViewById<EditText>(R.id.morningMilk)

        val eveningMilk =
            findViewById<EditText>(R.id.eveningMilk)

        val vaccinationDate =
            findViewById<EditText>(R.id.vaccinationDate)

        // BUTTONS

        val addCow =
            findViewById<Button>(R.id.addCow)

        val saveMilk =
            findViewById<Button>(R.id.saveMilk)

        val saveVaccination =
            findViewById<Button>(R.id.saveVaccination)

        val selectPhoto =
            findViewById<Button>(R.id.selectPhoto)

        val openDashboard =
            findViewById<Button>(R.id.openDashboard)

        // RESULT

        val resultText =
            findViewById<TextView>(R.id.resultText)

        // PHOTO

        val cowPhoto =
            findViewById<ImageView>(R.id.cowPhoto)

        // GRAPH

        val milkChart =
            findViewById<LineChart>(R.id.milkChart)

        // LOAD SAVED DATA

        val savedCow =
            sharedPreferences.getString(
                "cowName",
                ""
            )

        val savedId =
            sharedPreferences.getString(
                "cowId",
                ""
            )

        val savedAverage =
            sharedPreferences.getFloat(
                "averageMilk",
                0f
            )

        val savedVaccine =
            sharedPreferences.getString(
                "vaccinationDate",
                ""
            )

        if (savedCow != "") {

            resultText.text =
                "Saved Cow: $savedCow\n" +
                        "Cow ID: $savedId\n" +
                        "Monthly Average: $savedAverage L\n" +
                        "Vaccination: $savedVaccine"
        }

        // SELECT PHOTO

        selectPhoto.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(intent, 100)
        }

        // ADD COW

        addCow.setOnClickListener {

            val name =
                cowName.text.toString()

            val id =
                cowId.text.toString()

            // SAVE

            editor.putString("cowName", name)
            editor.putString("cowId", id)
            editor.apply()

            cowData =
                "Cow Name: $name\n" +
                        "Cow ID: $id"

            resultText.text =
                "$cowData\n\n$milkData\n\n$vaccineData"
        }

        // SAVE MILK

        saveMilk.setOnClickListener {

            val morning =
                morningMilk.text.toString()
                    .toFloatOrNull() ?: 0f

            val evening =
                eveningMilk.text.toString()
                    .toFloatOrNull() ?: 0f

            val totalMilk = morning + evening

            // STORE TOTAL MILK

            milkHistory.add(totalMilk)

            // MONTHLY AVERAGE

            val averageMilk =
                milkHistory.average().toFloat()

            // SAVE AVERAGE

            editor.putFloat(
                "averageMilk",
                averageMilk
            )

            editor.apply()

            milkData =
                "Morning Milk: $morning L\n" +
                        "Evening Milk: $evening L\n" +
                        "Total Milk: $totalMilk L\n" +
                        "Monthly Average Yield: $averageMilk L"

            resultText.text =
                "$cowData\n\n$milkData\n\n$vaccineData"

            // GRAPH

            val entries = ArrayList<Entry>()

            for (i in milkHistory.indices) {

                entries.add(
                    Entry(
                        i.toFloat(),
                        milkHistory[i]
                    )
                )
            }

            val dataSet =
                LineDataSet(entries, "Milk Yield Trend")

            val lineData =
                LineData(dataSet)

            milkChart.data = lineData

            milkChart.invalidate()
        }

        // SAVE VACCINATION

        saveVaccination.setOnClickListener {

            val vaccine =
                vaccinationDate.text.toString()

            // SAVE VACCINATION

            editor.putString(
                "vaccinationDate",
                vaccine
            )

            editor.apply()

            vaccineData =
                "Next Vaccination Date: $vaccine"

            resultText.text =
                "$cowData\n\n$milkData\n\n$vaccineData"

            try {

                val intent =
                    Intent(this, AlarmReceiver::class.java)

                val pendingIntent =
                    PendingIntent.getBroadcast(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                val alarmManager =
                    getSystemService(ALARM_SERVICE) as AlarmManager

                val calendar = Calendar.getInstance()

                // DEMO REMINDER AFTER 10 SECONDS

                calendar.add(Calendar.SECOND, 10)

                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )

                Toast.makeText(
                    this,
                    "Vaccination Reminder Set",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    "Reminder setup failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // OPEN DASHBOARD

        openDashboard.setOnClickListener {

            val intent =
                Intent(this, DashboardActivity::class.java)

            intent.putExtra(
                "cowName",
                cowName.text.toString()
            )

            intent.putExtra(
                "avgMilk",
                milkHistory.average().toFloat()
            )

            intent.putExtra(
                "vaccine",
                vaccinationDate.text.toString()
            )

            startActivity(intent)
        }
    }

    // IMAGE RESULT

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == 100 && data != null) {

            val imageUri = data.data

            val cowPhoto =
                findViewById<ImageView>(R.id.cowPhoto)

            cowPhoto.setImageURI(imageUri)
        }
    }
}