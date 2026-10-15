package com.cycletracker.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var temperatureChart: LineChart
    private lateinit var phaseText: android.widget.TextView
    private lateinit var coverlineText: android.widget.TextView
    private val analyzer = CycleAnalyzer()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("he", "IL"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("he", "IL"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(this)

        temperatureChart = findViewById(R.id.temperatureChart)
        phaseText = findViewById(R.id.phaseText)
        coverlineText = findViewById(R.id.coverlineText)
        val addButton: FloatingActionButton = findViewById(R.id.addButton)

        setupChart()
        loadData()

        addButton.setOnClickListener {
            showAddEntryDialog()
        }
    }

    private fun setupChart() {
        temperatureChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(false)
            setPinchZoom(true)
            legend.isEnabled = true

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
            }

            axisRight.isEnabled = false
            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 36f
                axisMaximum = 38f
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val entries = withContext(Dispatchers.IO) {
                database.temperatureDao().getAllEntries()
            }

            updateChart(entries)
            updateInsights(entries)
        }
    }

    private fun updateChart(entries: List<TemperatureEntry>) {
        if (entries.isEmpty()) {
            temperatureChart.clear()
            temperatureChart.invalidate()
            return
        }

        // Group by day and calculate average
        val dailyData = entries.groupBy {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it.date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.entries.sortedBy { it.key }.mapIndexed { index, entry ->
            Entry(index.toFloat() + 1, entry.value.map { it.temperature }.average().toFloat())
        }

        val dataSet = LineDataSet(dailyData, "טמפרטורת הגוף").apply {
            color = Color.rgb(233, 30, 99)
            setCircleColor(Color.rgb(233, 30, 99))
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(true)
            valueTextSize = 9f
            mode = LineDataSet.Mode.LINEAR
        }

        val lineData = LineData(dataSet)
        temperatureChart.data = lineData
        temperatureChart.invalidate()
    }

    private fun updateInsights(entries: List<TemperatureEntry>) {
        val insight = analyzer.analyzeCycle(entries)

        phaseText.text = insight.message

        if (insight.coverline != null) {
            coverlineText.text = "קו בסיס: %.2f°C".format(insight.coverline)
            coverlineText.visibility = android.view.View.VISIBLE
        } else {
            coverlineText.visibility = android.view.View.GONE
        }
    }

    private fun showAddEntryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_entry, null)
        val dateInput = dialogView.findViewById<TextInputEditText>(R.id.dateInput)
        val timeInput = dialogView.findViewById<TextInputEditText>(R.id.timeInput)
        val temperatureInput = dialogView.findViewById<TextInputEditText>(R.id.temperatureInput)

        val calendar = Calendar.getInstance()

        // Set default values
        dateInput.setText(dateFormat.format(calendar.time))
        timeInput.setText(timeFormat.format(calendar.time))

        // Date picker
        dateInput.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    dateInput.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Time picker
        timeInput.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    timeInput.setText(timeFormat.format(calendar.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.cancelButton)
            .setOnClickListener {
                dialog.dismiss()
            }

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.saveButton)
            .setOnClickListener {
                val tempText = temperatureInput.text.toString()

                if (tempText.isEmpty()) {
                    Toast.makeText(this, R.string.invalid_temperature, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val temperature = tempText.toDoubleOrNull()
                if (temperature == null || temperature < 35.0 || temperature > 42.0) {
                    Toast.makeText(this, R.string.invalid_temperature, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Normalize date to start of day for storage
                val dateOnly = Calendar.getInstance().apply {
                    timeInMillis = calendar.timeInMillis
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val entry = TemperatureEntry(
                    date = dateOnly.timeInMillis,
                    hour = calendar.get(Calendar.HOUR_OF_DAY),
                    minute = calendar.get(Calendar.MINUTE),
                    temperature = temperature
                )

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.temperatureDao().insertEntry(entry)
                    }
                    loadData()
                    Toast.makeText(this@MainActivity, R.string.entry_saved, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

        dialog.show()
    }
}
