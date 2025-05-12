package com.example.fotografpaylasma.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fotografpaylasma.R
import com.example.fotografpaylasma.adapter.PostAdapter
import com.example.fotografpaylasma.databinding.FragmentFeedBinding
import com.example.fotografpaylasma.model.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedFragment : Fragment(),PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var popup : PopupMenu
    private lateinit var aut : FirebaseAuth
    private  lateinit var  db : FirebaseFirestore
    val postList :ArrayList<Posts> = arrayListOf()
    private var adapter : PostAdapter ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aut = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { FloatingButtonTiklandi(it) }
        popup = PopupMenu(requireContext(),binding.floatingActionButton)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_popup_menu,popup.menu)
        popup.setOnMenuItemClickListener(this)
        FireStoreVerileriAl()

        adapter = PostAdapter(postList)
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedRecyclerView.adapter = adapter

    }

    private fun  FireStoreVerileriAl(){
            db.collection("Posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                if(error != null){
                    Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_LONG).show()
                }
                else{
                    if(value != null){
                        if(!value.isEmpty){
                            //BOŞ DEĞİLSE
                            postList.clear()

                            val documents  = value.documents

                            for (document in documents){
                                val comment = document.get("comment") as String
                                val email = document.get("email") as String
                                val posts = Posts(email,comment)
                                postList.add(posts)

                            }
                            adapter?.notifyDataSetChanged()
                        }
                    }

                }

            }
    }
    fun FloatingButtonTiklandi(view: View){

        popup.show()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.yuklemeItem){
            val action = FeedFragmentDirections.actionFeedFragmentToYuklemeFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }else if(item?.itemId == R.id.cikisItem){
            //cikis yapma islemi
            aut.signOut()
            val action = FeedFragmentDirections.actionFeedFragmentToKullaniciFragment()
            Navigation.findNavController(requireView()).navigate(action)


        }
        return true
    }

}