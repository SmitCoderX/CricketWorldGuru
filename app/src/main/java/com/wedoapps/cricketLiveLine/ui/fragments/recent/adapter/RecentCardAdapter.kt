package com.wedoapps.cricketLiveLine.ui.fragments.recent.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.RowRecentCardItemLayoutBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.model.Score
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import java.text.SimpleDateFormat
import java.util.*

class RecentCardAdapter(private val listener: SetOnClick) :
    RecyclerView.Adapter<RecentCardAdapter.RecentCardViewHolder>() {

    inner class RecentCardViewHolder(private val binding: RowRecentCardItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val storageRef = FirebaseStorage.getInstance().reference
        private var firestore = FirebaseFirestore.getInstance()
        private val firestoreRef = firestore.collection(Constants.COLLECTION_PATH_MATCH_LISTING)

        init {
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

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(match: HomeMatch) {

            binding.apply {
                tvMatch.text = match.matchDetail
                tvMatchStatus.text = "Finished"
                tvFirstTeam.text =  if(match.codeTeam1.isNullOrEmpty()) match.team1?.trim() else match.codeTeam1!!.trim()
                tvSecondTeam.text = if(match.codeTeam2.isNullOrEmpty()) match.team2?.trim() else match.codeTeam2!!.trim()

                fun Long?.getDateMatchList(): String {
                    val sdf = SimpleDateFormat("dd MMMM yyyy, h:mm a", Locale.ENGLISH)
                    val cal = Calendar.getInstance(Locale.ENGLISH)
                    cal.timeInMillis = this?.times(1000L) ?: 0L
                    return sdf.format(cal.time)
                }
                tvTime.text = match.matchDate?.getDateMatchList()


                storageRef.child("FlagIcon/" + match.team1?.trim { it <= ' ' } + ".png")
                    .downloadUrl.addOnSuccessListener { uri ->

                        Glide.with(itemView.context)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.imgpsh_fullsize_anim)
                            .into(ivFirstTeam)
                    }.addOnFailureListener {
                        ivFirstTeam.setImageDrawable(
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.imgpsh_fullsize_anim
                            )
                        )
                    }

                storageRef.child("FlagIcon/" + match.team2?.trim { it <= ' ' } + ".png")
                    .downloadUrl.addOnSuccessListener { uri ->

                        Glide.with(itemView.context)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.imgpsh_fullsize_anim)
                            .into(ivSecondTeam)
                    }.addOnFailureListener {
                        ivSecondTeam.setImageDrawable(
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.imgpsh_fullsize_anim
                            )
                        )
                    }
            }

            firestoreRef.document(match.id!!).collection("MatchRate").document("Match")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed", error)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        binding.apply {
                            tvFStrike.text = value.get("Rate1").toString().ifEmpty { "" }
                            tvSStrike.text = value.get("Rate2").toString().ifEmpty { "" }

                            if (match.matchResult.isNullOrEmpty()) {
                                tvDayStatus.text = ""
                            } else {
                                tvDayStatus.text = match.matchResult
                            }
                        }
                    }
                }

            firestoreRef.document(match.id!!).collection(Constants.COLLECTION_PATH_LIVE_MATCH).document("ScoreTeam1")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed", error)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val allTeam1 = value.toObject(Score::class.java)
                        binding.apply {
                            tvFInn.text = value.get("Score").toString()
                            tvOver.text = value.get("Over").toString() + " Over"
                        }
//                    val allTeam1 = ArrayList<Score>()
//                    allTeam1?.add(allTeam1)
                        Log.d(TAG, "team1: $allTeam1")
                    } else {
                        Log.d(TAG, "No Data")
                    }
                }

            firestoreRef.document(match.id!!).collection(Constants.COLLECTION_PATH_LIVE_MATCH).document("ScoreTeam2")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed", error)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val allTeam2 = value.toObject(Score::class.java)
                        binding.apply {
                            tvSecondFInn.text = value.get("Score").toString()
                            tvSecondOver.text = value.get("Over").toString() + " Over"
                        }
                        Log.d(TAG, "team2: $allTeam2")
                    } else {
                        Log.d(TAG, "No Data")
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentCardViewHolder {
        val binding = RowRecentCardItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentCardViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<HomeMatch>() {
        override fun areItemsTheSame(oldItem: HomeMatch, newItem: HomeMatch) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: HomeMatch, newItem: HomeMatch) =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface SetOnClick {
        fun onClick(match: HomeMatch)
    }

}