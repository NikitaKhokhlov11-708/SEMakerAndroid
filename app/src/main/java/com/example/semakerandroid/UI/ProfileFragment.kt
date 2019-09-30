package com.example.semakerandroid.UI

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.bumptech.glide.Glide
import com.example.semakerandroid.Models.User
import com.example.semakerandroid.Presenters.ProfilePresenter
import com.example.semakerandroid.ProfileView
import com.example.semakerandroid.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*


class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter

    @ProvidePresenterTag(presenterClass = ProfilePresenter::class)
    fun provideProfilePresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideProfilePresenter() = ProfilePresenter()

    val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val dbreference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users").child(fuser!!.uid)
    val storageReference: StorageReference = FirebaseStorage.getInstance().getReference("uploads")
    val IMAGE_REQUEST = 1
    lateinit var imageUri: Uri
    lateinit var uploadTask: UploadTask
    var isInitialized: Boolean = false
    private var root: View? = null

    companion object {
        fun newInstance(): ProfileFragment =
            ProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_profile, null)

        hide()

        dbreference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                root!!.tv_username.text = user?.username
                if (!isEmpty(user?.name))
                    root!!.userfio.text = user?.name
                if (!isEmpty(user?.birthdate))
                    root!!.userbirthdate.text = user?.birthdate
                if (!isEmpty(user?.sex))
                    root!!.usersex.text = user?.sex
                if (!isEmpty(user?.city))
                    root!!.usercity.text = user?.city
                if (!isEmpty(user?.about))
                    root!!.userabout.text = user?.about
                root!!.useremail.text = user?.email
                if (!isEmpty(user?.phone))
                    root!!.userphone.text = user?.phone
                if (user!!.imageURL == "default") {
                    root!!.profile_image.setImageResource(R.drawable.ic_launcher_background)
                } else {
                    context?.let {
                        Glide.with(it).load(user.imageURL).into(root!!.profile_image)
                    }
                }
                show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                context?.let {
                    Toast.makeText(
                        it, "Failed to read value.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        root!!.edit.setOnClickListener {
            val eProfileFragment = EditprofileFragment()
            val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, eProfileFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        root!!.profile_image.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, IMAGE_REQUEST)
        }

        return root
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver = context!!.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage() {
        val pd = ProgressDialog(context)
        pd.setMessage("Загрузка")
        pd.show()

        if (imageUri != null) {
            val fileReference =
                storageReference.child(
                    System.currentTimeMillis().toString() + "." + getFileExtension(
                        imageUri
                    )
                )
            uploadTask = fileReference.putFile(imageUri)
            isInitialized = true
            uploadTask.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                @Throws(Exception::class)
                override fun then(@NonNull task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    return fileReference.downloadUrl
                }
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val mUri = downloadUri.toString()

                    val map = HashMap<String, Any>()
                    map.put("imageURL", mUri)
                    dbreference.updateChildren(map)

                    pd.dismiss()
                } else {
                    context?.let {
                        Toast.makeText(
                            it, "Не удалось!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    pd.dismiss()
                }
            }.addOnFailureListener { e ->
                context?.let {
                    Toast.makeText(
                        it, e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    pd.dismiss()
                }
            }
        } else {
            context?.let {
                Toast.makeText(
                    it, "Изображение не выбрано",
                    Toast.LENGTH_SHORT
                ).show()
                pd.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data


            if (isInitialized) {
                if (uploadTask.isInProgress) {
                    context?.let {
                        Toast.makeText(
                            it, "Загрузка в процессе",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                uploadImage()
            }
        }
    }

    override fun hide() {
        root!!.linearLayout2.visibility = CoordinatorLayout.GONE
        root!!.progressBar.visibility = CoordinatorLayout.VISIBLE
    }

    override fun show() {
        root!!.linearLayout2.visibility = CoordinatorLayout.VISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_logout, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.frag1_item -> {
                FirebaseAuth.getInstance().signOut()
                context?.let {
                    val intent = Intent(it, LoginActivity::class.java)
                    startActivity(intent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
