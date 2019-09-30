package com.example.semakerandroid.UI

import android.app.Activity.RESULT_OK
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
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.AddEventView
import com.example.semakerandroid.Presenters.AddEventPresenter
import com.example.semakerandroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_addevent.view.*
import java.util.*


class AddeventFragment : MvpAppCompatFragment(), AddEventView {

    @InjectPresenter
    lateinit var addEventPresenter: AddEventPresenter

    @ProvidePresenterTag(presenterClass = AddEventPresenter::class)
    fun provideAddEventPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideAddEventPresenter() = AddEventPresenter()

    val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val dbreference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Events")
    val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("uploads")
    private var root: View? = null
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_addevent, null)

        addEventPresenter.getSports()

        root!!.choose.setOnClickListener {
            chooseImage()
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

        root!!.confirm.setOnClickListener {
            if (!TextUtils.isEmpty(root!!.et_name.text.toString())
                && !TextUtils.isEmpty(root!!.et_about.text.toString())
                && !TextUtils.isEmpty(root!!.et_city.text.toString())
                && !TextUtils.isEmpty(root!!.et_time.text.toString())
                && !TextUtils.isEmpty(root!!.et_places.text.toString())
            )
                uploadImage()
        }

        return root
    }

    private fun register(path: String) {
        val hashMap: HashMap<String, String> = HashMap()
        val key = dbreference.push().key
        hashMap["id"] = key!!
        hashMap["aid"] = fuser!!.uid
        hashMap["name"] = root!!.et_name.text.toString()
        hashMap["description"] = root!!.et_about.text.toString()
        hashMap["city"] = root!!.et_city.text.toString()
        hashMap["sid"] =
            addEventPresenter.list.indexOf(root!!.spinner.selectedItem.toString()).toString()
        hashMap["time"] = root!!.et_time.text.toString()
        hashMap["places"] = root!!.et_places.text.toString()
        hashMap["imageURL"] = path


        dbreference.child(key).setValue(hashMap).addOnCompleteListener { task ->
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data
        }
    }

    private fun uploadImage() {

        if (filePath != null) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Загрузка...")
            progressDialog.show()

            val ref = storageReference.child("images/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    ref.downloadUrl.addOnSuccessListener { uri -> register(uri.toString()) }
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

    override fun initializeSpinner() {
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item, addEventPresenter.list
        )

        root!!.spinner.adapter = adapter
    }
}