package com.example.diksha.firestoredemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val NAME_KEY: String = "NAME"
    val PHONE_KEY: String = "PHONE"
    val EMAIL_KEY: String = "EMAIL"

    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firestore = FirebaseFirestore.getInstance()

        save.setOnClickListener {
            val name = name.text.toString()
            val email = email.text.toString()
            val phone = phone.text.toString()

            addNewContact(name, email, phone) //adding to firestore
            addRealtimeUpdate()//real time update listener
            //ReadSingleContact() //reading from firestore
        }

        update.setOnClickListener {
            UpdateData()//updating fields in firestore
            addRealtimeUpdate()//real time update listener
        }

        delete.setOnClickListener { deleteData() } //delete from firestore
    }

    fun addNewContact(name : String, mail : String, phone : String) {

        val newContact = HashMap<String, Any>()

        newContact.put(NAME_KEY, name)
        newContact.put(EMAIL_KEY, mail)
        newContact.put(PHONE_KEY, phone)

        firestore.collection("PhoneBook")
                .document("Contacts")
                .set(newContact)
                .addOnSuccessListener( {Toast.makeText(this, "User Registered",
                        Toast.LENGTH_SHORT).show()})
                .addOnFailureListener( {e -> Toast.makeText(this, "ERROR" + e.toString(),
                        Toast.LENGTH_SHORT).show()})
    }

    fun ReadSingleContact() {

        val contact = firestore.collection("PhoneBook").document("Contacts")
        contact.get().addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                val doc = task.result
                val fields = StringBuilder("")

                fields.append("Name: ").append(doc.get("NAME"))
                        .append("\nEmail: ").append(doc.get("EMAIL"))
                        .append("\nPhone: ").append(doc.get("PHONE"))
                displayMessage.text = fields
            }
        }).addOnFailureListener({ })
    }

    fun UpdateData() {
        val contact = firestore.collection("PhoneBook").document("Contacts")
        contact.update(NAME_KEY, "Kenny")
        contact.update(EMAIL_KEY, "kenny@gmail.com")
        contact.update(PHONE_KEY, "090-911-419")
                .addOnSuccessListener({
                    Toast.makeText(this, "Updated Successfully",
                            Toast.LENGTH_SHORT).show()
                })
    }

    fun addRealtimeUpdate() {
        val contactListener = firestore.collection("PhoneBook").document("Contacts")

        contactListener.addSnapshotListener({ snapshots, e ->
                if (e != null) {
                    Log.d("ERROR", e.message)
                    return@addSnapshotListener
                }
                if (snapshots != null && snapshots.exists()) {
                    displayMessage.text = snapshots.data.toString()
                    Toast.makeText(this@MainActivity, "Current data:" + snapshots.data, Toast.LENGTH_SHORT).show()
                }
        })
    }

    fun deleteData() {
        firestore.collection("PhoneBook").document("Contacts")
                .delete().addOnSuccessListener { Toast.makeText(this,
                "Data Deleted", Toast.LENGTH_SHORT).show() }
    }
}
