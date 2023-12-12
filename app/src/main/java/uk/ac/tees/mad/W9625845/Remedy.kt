package uk.ac.tees.mad.W9625845

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase

class Remedy : AppCompatActivity() {

    private lateinit var diseaseAutoCompleteTextView: AutoCompleteTextView
    private lateinit var remedyTextView: TextView

    private val diseasesAndRemedies = mutableMapOf<String, String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remedy)


        diseaseAutoCompleteTextView = findViewById(R.id.diseaseAutoCompleteTextView)
        remedyTextView = findViewById(R.id.remedyTextView)
        fetchDiseasesFromFirebase()
        setupAutoCompleteTextView()
    }

    private fun fetchDiseasesFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("diseases")
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            dataSnapshot.children.forEach { snapshot ->
                val disease = snapshot.getValue(Disease::class.java) ?: return@forEach
                diseasesAndRemedies[disease.name] = disease.remedy
            }
            setupAutoCompleteTextView()
        }.addOnFailureListener {

        }
    }




    private fun setupAutoCompleteTextView() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            diseasesAndRemedies.keys.toList()
        )
        diseaseAutoCompleteTextView.setAdapter(adapter)

        diseaseAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val disease = adapter.getItem(position) ?: return@setOnItemClickListener
            val remedy = diseasesAndRemedies[disease]
            remedyTextView.text = remedy
        }
    }
}

data class Disease(
    val name: String = "",
    val remedy: String = ""
)
