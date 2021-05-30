package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback) {


    //Create ElectionDiffCallback
    companion object ElectionDiffCallback : DiffUtil.ItemCallback<Election>(){
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(ElectionListItemBinding.inflate(LayoutInflater.from(parent.context),
                                                                    parent, false))
    }

    //Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
       val election = getItem(position)
        holder.itemView.setOnClickListener{
            clickListener.onClick(election)
        }
        holder.bind(election)
    }

}

// Create ElectionViewHolder
class ElectionViewHolder(private var binding: ElectionListItemBinding): RecyclerView.ViewHolder(binding.root)
{

     fun bind(election:Election){
         binding.election = election
         binding.executePendingBindings()
     }
}


//Create ElectionListener
class ElectionListener (val clickListener: (election: Election) -> Unit)
{
    fun onClick(election:Election) = clickListener(election)
}