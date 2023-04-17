package com.wedoapps.cricketLiveLine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.cricketLiveLine.model.Session
import com.wedoapps.cricketLiveLine.databinding.HeaderSessionBinding
import com.wedoapps.cricketLiveLine.databinding.SessionItemLayoutBinding

class SessionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_LIST
    }

    inner class SessionHeaderViewHolder(private val binding: HeaderSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(session: Session) {
            binding.apply {
                if (session.min.isNullOrEmpty() && session.max.isNullOrEmpty() && session.name.isNullOrEmpty() && session.open.isNullOrEmpty() && session.complete.isNullOrEmpty()) {
                    root.visibility = View.GONE
                } else {
                    root.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class SessionViewHolder(private val binding: SessionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: Session) {
            binding.apply {
                tvOver.text = session.name
                tvOpen.text = session.open
                tvMin.text = session.min
                tvMax.text = session.max
                tvDone.text = session.complete
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val binding =
                HeaderSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SessionHeaderViewHolder(binding)
        }
        val binding =
            SessionItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is SessionHeaderViewHolder) {
            val currentItem = differ.currentList[position]
            holder.bind(currentItem)
        } else if (holder is SessionViewHolder) {
            val currentItem = differ.currentList[position - 1]
            holder.bind(currentItem)
        }
        /* if (currentItem != null) {
             holder.bind(currentItem)
         }*/
    }

    override fun getItemCount(): Int {
        return differ.currentList.size + 1
    }

    private val differCallback = object : DiffUtil.ItemCallback<Session>() {
        override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    companion object {
        const val TYPE_LIST: Int = 1
        const val TYPE_HEADER: Int = 0
    }
}