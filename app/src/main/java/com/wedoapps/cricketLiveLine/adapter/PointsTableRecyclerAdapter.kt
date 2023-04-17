package com.wedoapps.cricketLiveLine.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wedoapps.cricketLiveLine.model.Series.PointTableModel
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.HeaderPointTableBinding
import com.wedoapps.cricketLiveLine.databinding.LayoutPointsTableBinding

class PointsTableRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private var firestore = FirebaseFirestore.getInstance()
    private val firestoreRef = firestore.collection("MatchList")

    override fun getItemViewType(position: Int): Int {
        return if (position == HEADER) {
            HEADER
        } else {
            ITEM
        }
    }

    inner class PointsTableViewHolder(private val binding: LayoutPointsTableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(table: PointTableModel) {
            binding.apply {

                storageRef.child("FlagIcon/" + table.teamName?.trim { it <= ' ' } + ".png")
                    .downloadUrl.addOnSuccessListener { uri ->

                        Glide.with(itemView.context)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.imgpsh_fullsize_anim)
                            .into(ivTeamImg)
                    }.addOnFailureListener {
                        ivTeamImg.setImageDrawable(
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.imgpsh_fullsize_anim
                            )
                        )
                    }

                tvPtTeamName.text = table.teamName?.trim()
                tvPtPlayed.text = table.matchPlayed?.trim()
                tvPtWon.text = table.matchWin?.trim()
                tvPtLost.text = table.matchLose?.trim()
                tvPtTied.text = table.matchTie?.trim()
                tvPtPoints.text = table.matchPTS.toString()
                tvPtRate.text = table.matchNrr?.trim()
            }
        }
    }

    inner class PointsTableHeaderViewHolder(private val binding: HeaderPointTableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER) {
            val binding =
                HeaderPointTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PointsTableHeaderViewHolder(binding)
        }
        val binding =
            LayoutPointsTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PointsTableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        if (position == HEADER) {
            val headerHolder = holder as PointsTableHeaderViewHolder
            headerHolder.bind()
        } else {
            val itemHolder = holder as PointsTableViewHolder
            itemHolder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    companion object {
        const val HEADER = 0
        const val ITEM = 1
    }

    private val differCallback = object : DiffUtil.ItemCallback<PointTableModel>() {
        override fun areItemsTheSame(oldItem: PointTableModel, newItem: PointTableModel): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: PointTableModel, newItem: PointTableModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

}