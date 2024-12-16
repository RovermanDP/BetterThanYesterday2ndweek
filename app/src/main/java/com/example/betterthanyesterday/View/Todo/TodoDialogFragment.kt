package com.example.betterthanyesterday.View.Todo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.betterthanyesterday.R
import com.example.betterthanyesterday.databinding.FragmentTodoDialogBinding
import com.example.betterthanyesterday.Viewmodel.TodoViewModel
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class TodoDialogFragment : DialogFragment() {

    val viewModel: TodoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTodoDialogBinding.inflate(inflater, container, false)

        val text1 = arguments?.getString("text1")
        val text2 = arguments?.getString("text2")
        val imageUrl = arguments?.getString("imgUri")

        binding.edttxtTitle.setText(text1)
        binding.edttxtDetail.setText(text2)

        val imageView = binding.imageView2 // 이미지 뷰를 연결
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(imageUrl) // Firebase에서 가져온 이미지 URL
                .into(imageView) // 이미지를 ImageView에 표시
        }

        binding.btnChk.setOnClickListener {
            val edtTitle = binding.edttxtTitle.text.toString()
            val edtDetail = binding.edttxtDetail.text.toString()

            // 입력된 제목과 세부 내용이 비어있지 않은지 확인
            if (edtTitle.isNotBlank() || edtDetail.isNotBlank()) {
                // 제목 또는 세부 내용이 변경된 경우에만 업데이트 수행
                if (edtTitle != text1 || edtDetail != text2) {
                    val database = FirebaseDatabase.getInstance().reference
                    val query = database.child("todo").orderByChild("title").equalTo(text1)

                    query.get().addOnSuccessListener { dataSnapshot ->
                        for (childSnapshot in dataSnapshot.children) {
                            val key = childSnapshot.key
                            if (key != null) {
                                val updatedData = mapOf(
                                    "title" to edtTitle,
                                    "detail" to edtDetail
                                )
                                database.child("todo").child(key).updateChildren(updatedData)
                                    .addOnSuccessListener {
                                        Log.d("Firebase", "Data updated successfully")
                                        dismiss() // 다이얼로그 닫기
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firebase", "Error updating data", e)
                                    }
                            }
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("Firebase", "Query failed", exception)
                    }
                } else {
                    dismiss() // 변경된 내용이 없을 경우에도 다이얼로그 닫기
                }
            }
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt() // 화면 너비의 80%
        val height = (resources.displayMetrics.heightPixels * 0.7).toInt() // 화면 높이의 70%

        dialog.window?.setLayout(width, height)
    }

    companion object {
        @JvmStatic
        fun newInstance(str1: String? = null, str2: String? = null, imgUri: String? = null): TodoDialogFragment {
            val frag = TodoDialogFragment()
            val args = Bundle().apply {
                putString("text1", str1)
                putString("text2", str2)
                putString("imgUri", imgUri)
            }
            frag.arguments = args
            return frag
        }
    }
}