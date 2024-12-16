package com.example.betterthanyesterday.View.Todo

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.betterthanyesterday.R
import com.example.betterthanyesterday.databinding.FragmentTodoAddBinding
import com.example.betterthanyesterday.Viewmodel.Todo
import com.example.betterthanyesterday.Viewmodel.TodoViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class TodoAddFragment : Fragment() {

    val viewModel: TodoViewModel by activityViewModels()

    private lateinit var storageRef: StorageReference

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTodoAddBinding.inflate(inflater, container, false)

        storageRef = FirebaseStorage.getInstance().getReference("image")

        val text1 = arguments?.getString("text1")
        val text2 = arguments?.getString("text2")

        binding.edtTitle.setText(text1)
        binding.edtDetail.setText(text2)

        // 사진 선택
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.imageView.setImageURI(it)
            if (it != null) {
                uri = it
            }
        }

        binding.addImgbtn.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.addBtn.setOnClickListener {
            val newTitle = binding.edtTitle.text.toString()
            val newDetail = binding.edtDetail.text.toString()

            if (newTitle.isNotBlank()) {
                // 사진 있을 때 Todo 추가
                if (uri != null) {
                    val imageRef = storageRef.child("todo/${System.currentTimeMillis()}.jpg")
                    imageRef.putFile(uri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                val imgUri = downloadUri.toString()
                                val newTodo = Todo(newTitle, newDetail, imgUri)
                                viewModel.addTodo(newTodo)
                                findNavController().navigate(R.id.action_todoAddFragment_to_todoFragment)
                            }
                        }
                        .addOnFailureListener { exception ->
                            exception.printStackTrace()
                        }
                // 사진 없을 때 Todo 추가
                } else {
                    val newTodo = Todo(newTitle, newDetail, null)
                    viewModel.addTodo(newTodo)
                    findNavController().navigate(R.id.action_todoAddFragment_to_todoFragment)
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(str1: String? = null, str2: String? = null): TodoAddFragment {
            val frag = TodoAddFragment()
            val args = Bundle().apply {
                putString("text1", str1)
                putString("text2", str2)
            }
            frag.arguments = args
            return frag
        }
    }

}