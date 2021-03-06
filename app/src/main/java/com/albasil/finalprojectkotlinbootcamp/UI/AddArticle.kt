package com.albasil.finalprojectkotlinbootcamp.UI

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.albasil.finalprojectkotlinbootcamp.R
import com.albasil.finalprojectkotlinbootcamp.ViewModels.AddArticleViewModel
import com.albasil.finalprojectkotlinbootcamp.data.Article
import com.albasil.finalprojectkotlinbootcamp.databinding.FragmentAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddArticle : Fragment() {

    private lateinit var addArticleViewModel: AddArticleViewModel

    // private  var imageUrl : Uri?=null
    lateinit var binding: FragmentAddArticleBinding
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    var categorySelected: String? = null


    private var imageUrl: Uri? = null


    // Date  object
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatted = current.format(formatter)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddArticleBinding.inflate(inflater, container, false)
        //getUserName()
        binding.tvDateXml.setText(" Date :${formatted}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addArticleViewModel = ViewModelProvider(this).get(AddArticleViewModel::class.java)
        //----------------------------------------------------------------------------------------
        val category = resources.getStringArray(R.array.addCategories)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, category)

        binding.spinnerCategoryXml.setAdapter(arrayAdapter)

        binding.spinnerCategoryXml.onItemClickListener =
            object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    categorySelected = "${category[position]}"

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemClick(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    categorySelected = "${category[position]}"
                }
            }
//-------------------------------------------------------------------------------------------------------

        binding.btnAddArticleXml.setOnClickListener {

            checkFields()
        }

        binding.articlerPhotoXml.setOnClickListener {

            selectImage()

        }
    }

    fun checkFields() {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-SS")
        val formatted2 = current.format(formatter)

        when {
            TextUtils.isEmpty(binding.etTitleArticleXml.text.toString().trim { it <= ' ' }) -> {
                binding.tvTitleArticleXml.helperText = "Title Article is null"

            }
            TextUtils.isEmpty(
                binding.etDescraptaionArticleXml.text.toString().trim { it <= ' ' }) -> {
                binding.tvDescraptaionArticleXml.helperText = "Descraptaion Article is null"

            }
            else -> {
                if (categorySelected.isNullOrEmpty()) {
                    Toast.makeText(context, "Please Select Category", Toast.LENGTH_LONG).show()

                } else {

                    val UUID = UUID.randomUUID().toString()
                    if (imageUrl != null) {

                        articleData(
                            "${categorySelected.toString()}",
                            "${binding.etTitleArticleXml.text.toString()}",
                            "${binding.etDescraptaionArticleXml.text.toString()}",
                            "${UUID.toString()}",
                            "${UUID}"
                        )
                    } else {
                        articleData(
                            "${categorySelected.toString()}",
                            "${binding.etTitleArticleXml.text.toString()}",
                            "${binding.etDescraptaionArticleXml.text.toString()}",
                            "${UUID}"
                        )
                    }


                }
            }
        }
    }

    fun articleData(
        category: String,
        title: String,
        description: String,
        articleID: String,
        articlePhotoID: String? = ""
    ) {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val article = Article()
        //article.userName =userNameGlobl.toString()
        article.category = category.toString()
        article.userId = userId.toString()
        article.date = formatted.toString()
        article.description = description.toString()
        article.title = title
        article.articleID = articleID
        article.articleImage = articlePhotoID.toString()


        //view Model
        view?.let { addArticleViewModel.addArticle(article, it) }

        val fileName = articlePhotoID.toString()//System.currentTimeMillis().toString()
        Log.e("TAG", articlePhotoID.toString().toString())


        imageUrl?.let {
            //progressDialog.show()
            addArticleViewModel.uploadArticleImage(imageUrl!!, fileName)
        }

        findNavController().popBackStack()

        binding.etTitleArticleXml.text = null
        binding.etDescraptaionArticleXml.text = null
        binding.spinnerCategoryXml.text = null
        binding.articlerPhotoXml.setImageURI(null)

    }

    //images
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUrl = data?.data!!
            binding.articlerPhotoXml.setImageURI(imageUrl)
        }
    }

    fun upLoadImage(imageId: String) {
        Toast.makeText(context, "upLoadImage ${imageId.toString()}", Toast.LENGTH_SHORT).show()
        val storageReference =
            FirebaseStorage.getInstance().getReference("imagesArticle/${imageId}")
        if (imageUrl != null) {
            imageUrl?.let {
                storageReference.putFile(it)
                    .addOnSuccessListener {
                    }.addOnFailureListener {
                    }
            }
        }
//        imageUrl?.let {
//            storageReference.putFile(it)
//                .addOnSuccessListener {
//                }.addOnFailureListener{
//                }
//        }
    }
}