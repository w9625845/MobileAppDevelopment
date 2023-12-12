package uk.ac.tees.mad.W9625845

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream


class AskADoctor : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var spinnerDoctor: Spinner
    private lateinit var imageViewProof: ImageView
    private var imageString: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_adoctor)

        editTextName = findViewById(R.id.editTextName)
        spinnerDoctor = findViewById(R.id.spinnerDoctor)
        imageViewProof = findViewById(R.id.imageViewProof)

        setupDoctorSpinner()

        findViewById<Button>(R.id.buttonCaptureImage).setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, 440)
        }

        findViewById<Button>(R.id.buttonStartConsulting).setOnClickListener {
            startConsulting()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 440 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageViewProof.setImageBitmap(imageBitmap)
            ByteArrayOutputStream().apply {
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
                imageString =  Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)
            }
        }
    }


    private fun setupDoctorSpinner() {
        val doctorNames = listOf("Dr. Smith- Orthopedic", "Dr. Johnson -  Deontologist", "Dr. Williams - Gynecology", "Dr. Brown - Neurosurgeon ", "Dr. Jones - Ortho")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            doctorNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDoctor.adapter = adapter
    }
    fun startConsulting()
    {

        val name = editTextName.text.toString()
        val selectedDoctor = spinnerDoctor.selectedItem.toString()


        val consultationData = mapOf(
            "name" to name,
            "doctor" to selectedDoctor,
            "imageBase64" to imageString
        )

        // Reference to your Firebase Realtime Database path
        val databaseReference = FirebaseDatabase.getInstance().getReference("onlineConsultations")

        // Pushing the data to Firebase
        databaseReference.push().setValue(consultationData)
            .addOnSuccessListener {

                Snackbar.make(findViewById(android.R.id.content), "Your Question is on queue, doctor will respond via mail", Snackbar.LENGTH_LONG).show()


                Handler().postDelayed({

                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                },  3000)
            }
            .addOnFailureListener { e ->

                Toast.makeText(this, "Failed to Ask Question ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}