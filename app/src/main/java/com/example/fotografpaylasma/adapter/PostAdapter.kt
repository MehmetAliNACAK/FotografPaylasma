package com.example.fotografpaylasma.adapter

import android.renderscript.ScriptGroup.Binding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fotografpaylasma.databinding.RecycleRowBinding
import com.example.fotografpaylasma.model.Posts

class PostAdapter (private val postList : ArrayList<Posts> ) : RecyclerView.Adapter<PostAdapter.PostHolder>() {


    class PostHolder (val binding :RecycleRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecycleRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return PostHolder(binding)
    }

    override fun getItemCount(): Int {

        return  postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.binding.recyclerEmailText.text = postList[position].email
        holder.binding.recyclerCommentText.text = postList[position].comment



    }
}