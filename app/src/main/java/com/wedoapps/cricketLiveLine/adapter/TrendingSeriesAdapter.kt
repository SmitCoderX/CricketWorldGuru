package com.wedoapps.cricketLiveLine.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.wedoapps.cricketLiveLine.model.Series.SeriesModel
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.LayoutTrendingSeriesBinding
import com.wedoapps.cricketLiveLine.utils.Constants

class TrendingSeriesAdapter(private val listener: OnSeriesClick) :
    RecyclerView.Adapter<TrendingSeriesAdapter.TrendingViewHolder>() {

    private val storageRef = FirebaseStorage.getInstance().reference
    inner class TrendingViewHolder(private val binding: LayoutTrendingSeriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(seriesModel: SeriesModel) {
            binding.apply {
                tvSeriesName.text = seriesModel.seriesName?.trim()
                if(seriesModel.year.isNullOrEmpty() || seriesModel.year.equals("-")) {
                    tvTotalMatchesDates.text =
                        "${seriesModel.totalMatches} Matches • ${seriesModel.seriesFromDate} - ${seriesModel.seriesToDate}"
                } else {
                    tvTotalMatchesDates.text =
                        "${seriesModel.totalMatches} Matches • ${seriesModel.seriesFromDate} - ${seriesModel.seriesToDate} ${seriesModel.year}"
                }


                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = differ.currentList[position]
                        if (item != null) {
                            listener.onSeriesItemClick(item)
                        }
                    }
                }

                storageRef.child("SeriesIcon/" + seriesModel.seriesName?.trim { it <= ' ' } + ".png")
                    .downloadUrl.addOnSuccessListener { uri ->
                        Log.d(Constants.TAG, "TeamH1URL: $uri")
                        Glide.with(itemView.context)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.imgpsh_fullsize_anim)
                            .into(ivSeriesImage)
                    }.addOnFailureListener {
                        ivSeriesImage.setImageDrawable(
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.imgpsh_fullsize_anim
                            )
                        )
                    }


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding =
            LayoutTrendingSeriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val differCallback = object : DiffUtil.ItemCallback<SeriesModel>() {
        override fun areItemsTheSame(oldItem: SeriesModel, newItem: SeriesModel): Boolean {
            return oldItem.seriesId == newItem.seriesId
        }

        override fun areContentsTheSame(oldItem: SeriesModel, newItem: SeriesModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnSeriesClick {
        fun onSeriesItemClick(seriesModel: SeriesModel)
    }
}