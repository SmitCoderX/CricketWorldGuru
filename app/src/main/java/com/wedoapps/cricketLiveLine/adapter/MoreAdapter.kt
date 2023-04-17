package com.wedoapps.cricketLiveLine.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.LayoutMoreBinding
import com.wedoapps.cricketLiveLine.model.MoreData

class MoreAdapter(private val listener: OnSetClick) :
    RecyclerView.Adapter<MoreAdapter.MoreViewHolder>() {

    inner class MoreViewHolder(private val binding: LayoutMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MoreData) {
            binding.apply {

                tvTitle.text = data.name

                Glide.with(itemView.context)
                    .load(data.img)
                    .centerCrop()
                    .placeholder(R.drawable.imgpsh_fullsize_anim)
                    .into(ivImg)

                binding.root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = differ.currentList[position]
                        if (item != null) {
                            listener.onClick(item)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreViewHolder {
        val binding =
            LayoutMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoreViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MoreViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<MoreData>() {
        override fun areItemsTheSame(oldItem: MoreData, newItem: MoreData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MoreData, newItem: MoreData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnSetClick {
        fun onClick(data: MoreData)
    }

}