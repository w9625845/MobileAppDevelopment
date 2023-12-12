package uk.ac.tees.mad.W9625845

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.compose.material3.Snackbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar


class Appointment : AppCompatActivity() {

    private lateinit var editTextPatientName: EditText
    private lateinit var editTextAppointmentDate: EditText
    private lateinit var editTextAppointmentTime: EditText
    private lateinit var spinnerHospital: Spinner
    private lateinit var spinnerDoctor: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        editTextPatientName = findViewById(R.id.editTextPatientName)
        editTextAppointmentDate = findViewById(R.id.editTextAppointmentDate)
        editTextAppointmentTime = findViewById(R.id.editTextAppointmentTime)
        spinnerHospital = findViewById(R.id.spinnerHospital)
        spinnerDoctor = findViewById(R.id.spinnerDoctor)

        editTextAppointmentDate.setOnClickListener {
            showDatePicker()
        }

        editTextAppointmentTime.setOnClickListener {
            showTimePicker()
        }
        setupHospitalSpinner()

        val buttonBookAppointment = findViewById<Button>(R.id.buttonBookAppointment)
        buttonBookAppointment.setOnClickListener {
            saveAppointmentToDatabase()
        }

    }

    private fun saveAppointmentToDatabase() {
        val patientName = editTextPatientName.text.toString()
        val appointmentDate = editTextAppointmentDate.text.toString()
        val appointmentTime = editTextAppointmentTime.text.toString()
        val selectedHospital = spinnerHospital.selectedItem.toString()
        val selectedDoctor = spinnerDoctor.selectedItem.toString()

        val appointmentInfo = mapOf(
            "patientName" to patientName,
            "date" to appointmentDate,
            "time" to appointmentTime,
            "hospital" to selectedHospital,
            "doctor" to selectedDoctor
        )

        val databaseReference = FirebaseDatabase.getInstance().getReference("appointments")
        val appointmentId = databaseReference.push().key ?: return
        databaseReference.child(appointmentId).setValue(appointmentInfo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showConfirmationSnackbar()
                } else {
                    Toast.makeText(this, "Failed to book appointment", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun showConfirmationSnackbar() {
        Snackbar.make(findViewById(android.R.id.content), "Appointment booked successfully", Snackbar.LENGTH_LONG)
            .show()

        Handler(Looper.getMainLooper()).postDelayed({
           finish()
        }, 2000)
    }


    private fun setupHospitalSpinner() {
        val hospitals = arrayOf("Select Hospital","Brooks Hospital", "care Hospital", "Johns Hospital")
        val hospitalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hospitals)
        hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHospital.adapter = hospitalAdapter

        spinnerHospital.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                setupDoctorSpinner(hospitals[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupDoctorSpinner(selectedHospital: String) {
        val doctorsByHospital = mapOf(
            "Select Hospital" to arrayOf("Select Doctor"),
            "Brooks Hospital" to arrayOf("Dr. Smith - Cardiology", "Dr. Johnson - Neurology"),
            "care Hospital" to arrayOf("Dr. Williams - Pediatrics", "Dr. Brown - General"),
            "Johns Hospital" to arrayOf("Dr. Davis - Oncology", "Dr. Miller - Dermatology")
        )

        val doctors = doctorsByHospital[selectedHospital] ?: arrayOf()
        val doctorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctors)
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDoctor.adapter = doctorAdapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            editTextAppointmentDate.setText("${dayOfMonth}/${month + 1}/$year")
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            editTextAppointmentTime.setText("$hourOfDay:$minute")
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }



}