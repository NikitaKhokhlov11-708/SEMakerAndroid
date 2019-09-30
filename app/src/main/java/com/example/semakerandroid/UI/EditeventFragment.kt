package com.example.semakerandroid.UI

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.EditEventView
import com.example.semakerandroid.Models.Event
import com.example.semakerandroid.Presenters.EditEventPresenter
import com.example.semakerandroid.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_addevent.view.*
import java.util.*

class EditeventFragment : MvpAppCompatFragment(), EditEventView {

    @InjectPresenter
    lateinit var editEventPresenter: EditEventPresenter

    @ProvidePresenterTag(presenterClass = EditEventPresenter::class)
    fun provideEditEventPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideEditEventPresenter() = EditEventPresenter()

    val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val dbreference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Events")
    val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("uploads")
    private var root: View? = null
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    lateinit var event: Event

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_addevent, null)

        hide()

        val bundle = arguments
        event = bundle!!.getSerializable("event") as Event

        editEventPresenter.getEventData()

        root!!.et_name.setText(event.name)
        root!!.et_about.setText(event.description)
        root!!.et_city.setText(event.city)
        root!!.et_about.setText(event.description)
        root!!.et_time.text = event.time
        root!!.et_places.setText(event.places)

        root!!.choose.setOnClickListener {
            chooseImage()
        }

        root!!.confirm.setOnClickListener {
            if (!TextUtils.isEmpty(root!!.et_name.text.toString())
                && !TextUtils.isEmpty(root!!.et_about.text.toString())
                && !TextUtils.isEmpty(root!!.et_city.text.toString())
                && !TextUtils.isEmpty(root!!.et_time.text.toString())
                && !TextUtils.isEmpty(root!!.et_places.text.toString())
            )
                uploadImage()
        }

        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                root!!.et_time.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1
        )

        root!!.et_time.setOnClickListener {
            datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePickerDialog.show()
        }

        show()

        return root
    }

    private fun register(path: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["id"] = event.id!!
        hashMap["aid"] = fuser!!.uid
        hashMap["name"] = root!!.et_name.text.toString()
        hashMap["description"] = root!!.et_about.text.toString()
        hashMap["city"] = root!!.et_city.text.toString()
        hashMap["sid"] =
            editEventPresenter.list.indexOf(root!!.spinner.selectedItem.toString()).toString()
        hashMap["time"] = root!!.et_time.text.toString()
        hashMap["places"] = root!!.et_places.text.toString()
        if (!TextUtils.isEmpty(path))
            hashMap["imageURL"] = path
        else hashMap["imageURL"] = event.imageURL!!


        dbreference.child(event.id!!).setValue(hashMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val organizationFragment = OrganizationFragment()
                val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container, organizationFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data
        }
    }

    override fun uploadImage() {

        if (filePath != null) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            val ref = storageReference.child("images/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    ref.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        override fun onSuccess(uri: Uri) {
                            register(uri.toString())
                        }
                    })
                    context?.let {
                        Toast.makeText(
                            it, "Загружено",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    context?.let {
                        Toast.makeText(it, "Ошибка " + e.message, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                    progressDialog.setMessage("Загружено " + progress.toInt() + "%")
                }
        }
    }

    override fun initSpinner() {
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item, editEventPresenter.list
        )

        root!!.spinner.adapter = adapter
    }

    override fun hide() {
        root!!.linearLayout.visibility = CoordinatorLayout.INVISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.VISIBLE
    }

    override fun show() {
        root!!.linearLayout.visibility = CoordinatorLayout.VISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.INVISIBLE
    }
}