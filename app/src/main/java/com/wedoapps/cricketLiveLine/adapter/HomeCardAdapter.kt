package com.wedoapps.cricketLiveLine.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.LayoutHomeItemBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.model.Score
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import java.text.SimpleDateFormat
import java.util.*


class HomeCardAdapter(private val listener: SetOnClick) :
    RecyclerView.Adapter<HomeCardAdapter.HomeCardViewHolder>() {

    inner class HomeCardViewHolder(private val binding: LayoutHomeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val storageRef = FirebaseStorage.getInstance().reference
        private var firestore = FirebaseFirestore.getInstance()
        private val firestoreRef = firestore.collection("MatchList")

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

        @SuppressLint("SetTextI18n", "SimpleDateFormat", "NewApi")
        fun bind(match: HomeMatch) {

            when (match.matchStatus) {
                "UPCOMING" -> {

                    binding.tvFInn.isSingleLine = false
                    binding.tvSecondFInn.isSingleLine = false
                    binding.tvOver.visibility = View.GONE
                    binding.tvSecondOver.visibility = View.GONE
                    binding.tvFirstTeam.visibility = View.INVISIBLE
                    binding.tvSecondTeam.visibility = View.INVISIBLE
                    binding.tvRate1.visibility = View.VISIBLE
                    binding.tvRate2.visibility = View.VISIBLE
                    binding.tvFavTeam.visibility = View.VISIBLE
                    binding.tvDayStatus.visibility = View.VISIBLE
                    binding.tvFInn.gravity = Gravity.CENTER_VERTICAL
                    binding.tvFInn.gravity = Gravity.START
                    binding.tvSecondFInn.gravity = Gravity.CENTER_VERTICAL
                    binding.tvSecondFInn.gravity = Gravity.END
                    binding.tvMatchStatus.text = "Upcoming"
                    binding.tvMatchStatus.backgroundTintList =
                        ColorStateList.valueOf(itemView.context.getColor(R.color.yellow_orange))
                    binding.tvFInn.text =
                        if (match.fullNameTeam1.isNullOrEmpty()) match.team1?.trim() else match.fullNameTeam1!!.trim()
                    binding.tvSecondFInn.text =
                        if (match.fullNameTeam2.isNullOrEmpty()) match.team2?.trim() else match.fullNameTeam2!!.trim()
                    binding.tvFInn.maxLines = 3
                    binding.tvSecondFInn.maxLines = 3
                    binding.tvFInn.setLineSpacing(0.8f, 0.8f)
                    binding.tvSecondFInn.setLineSpacing(0.8f, 0.8f)
                }
                "COMPLETED" -> {
                    binding.tvFInn.maxLines = 1
                    binding.tvSecondFInn.maxLines = 1
                    binding.tvFInn.isSingleLine = true
                    binding.tvSecondFInn.isSingleLine = true
                    binding.tvOver.visibility = View.VISIBLE
                    binding.tvSecondOver.visibility = View.VISIBLE
                    binding.tvFirstTeam.visibility = View.VISIBLE
                    binding.tvSecondTeam.visibility = View.VISIBLE
                    binding.tvFInn.gravity = Gravity.START
                    binding.tvSecondFInn.gravity = Gravity.END
                    binding.tvRate1.visibility = View.GONE
                    binding.tvRate2.visibility = View.GONE
                    binding.tvFavTeam.visibility = View.GONE
                    binding.tvMatchStatus.text = "Finished"
                    binding.tvMatchStatus.backgroundTintList =
                        ColorStateList.valueOf(itemView.context.getColor(R.color.green))
                    binding.tvFirstTeam.text =
                        if (match.codeTeam1.isNullOrEmpty()) match.team1?.trim() else match.codeTeam1!!.trim()
                    binding.tvSecondTeam.text =
                        if (match.codeTeam2.isNullOrEmpty()) match.team2?.trim() else match.codeTeam2!!.trim()

                    setScore(match.id!!)

                }
                "LIVE" -> {
                    binding.tvFInn.maxLines = 1
                    binding.tvSecondFInn.maxLines = 1
                    binding.tvFInn.isSingleLine = true
                    binding.tvSecondFInn.isSingleLine = true
                    binding.tvOver.visibility = View.VISIBLE
                    binding.tvSecondOver.visibility = View.VISIBLE
                    binding.tvFirstTeam.visibility = View.VISIBLE
                    binding.tvSecondTeam.visibility = View.VISIBLE
                    binding.tvRate1.visibility = View.VISIBLE
                    binding.tvRate2.visibility = View.VISIBLE
                    binding.tvFavTeam.visibility = View.VISIBLE
                    binding.tvDayStatus.visibility = View.VISIBLE
                    binding.tvFInn.gravity = Gravity.START
                    binding.tvSecondFInn.gravity = Gravity.END
                    binding.tvMatchStatus.text = "Live"
                    binding.tvMatchStatus.backgroundTintList =
                        ColorStateList.valueOf(itemView.context.getColor(R.color.colorRed))
                    binding.tvFirstTeam.text =
                        if (match.codeTeam1.isNullOrEmpty()) match.team1?.trim() else match.codeTeam1!!.trim()
                    binding.tvSecondTeam.text =
                        if (match.codeTeam2.isNullOrEmpty()) match.team2?.trim() else match.codeTeam2!!.trim()

                    setScore(match.id!!)
                }
                else -> {
                    binding.tvMatchStatus.text = match.matchStatus?.trim()
                }
            }

            firestoreRef.document(match.id!!).collection("MatchRate").document("Match")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed", error)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val data = value.data
                        if (data != null) {
                            val it = data.toString()
                            val value1 = it.substring(1, it.length - 1)
                            val keyValuePair = value1.split(",")
                            val map = hashMapOf<String, String>()

                            keyValuePair.forEach { text ->
                                val entry = text.split("=")
                                map[entry[0].trim()] = entry[1].trim()
                            }
                            binding.tvFavTeam.text = if(map["FavTeam"]?.trim().isNullOrEmpty() || map["FavTeam"].equals("")) "" else map["FavTeam"]?.trim()
                            binding.tvRate1.text = if(map["Rate1"]?.trim().isNullOrEmpty() || map["Rate1"].equals("")) "-" else map["Rate1"]?.trim()
                            binding.tvRate2.text = if(map["Rate2"]?.trim().isNullOrEmpty() || map["Rate2"].equals("")) "-" else map["Rate2"]?.trim()


                            Log.d(TAG, "mr: $data")
                        }
                    } else {
                        Log.d(TAG, "No Data")
                    }
                }

            firestoreRef.document(match.id!!).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
                .document("RunRateInfo")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed", error)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val data = value.data
                        if (data != null) {
                            val it = data.toString()
                            val value1 = it.substring(1, it.length - 1)
                            val keyValuePair = value1.split(",")
                            val map = hashMapOf<String, String>()

                            keyValuePair.forEach { text ->
                                val entry = text.split("=")
                                map[entry[0].trim()] = entry[1].trim()
                            }

                                binding.tvDayStatus.text = if(map["OtherInfo"]?.trim().isNullOrEmpty() || map["OtherInfo"].equals("") || map["OtherInfo"].equals("-")) if(match.venue.isNullOrEmpty()) "" else "${match.venue}" else map["OtherInfo"]?.trim()
                            Log.d(TAG, "getRunRate: $data")
                        }
                    } else {
                        Log.d(TAG, "No Data")
                    }
                }

            binding.apply {
                tvMatch.text = match.matchDetail?.trim()

                if(match.matchDate == null) {
                    tvTime.text = match.currentDate?.trim()
                } else {
                    fun Long?.getDateMatchList(): String {
                        val sdf = SimpleDateFormat("dd MMMM yyyy, h:mm a", Locale.ENGLISH)
                        val cal = Calendar.getInstance(Locale.ENGLISH)
                        cal.timeInMillis = this?.times(1000L) ?: 0L
                        return sdf.format(cal.time)
                    }
                    tvTime.text = match.matchDate.getDateMatchList()
                }



                storageRef.child("FlagIcon/" + match.team1?.trim { it <= ' ' } + ".png")
                    .downloadUrl.addOnSuccessListener { uri ->
                        Log.d(TAG, "TeamH1URL: $uri")
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
                        Log.d(TAG, "TeamH2URL: $uri")
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


        }

        @SuppressLint("SetTextI18n")
        private fun setScore(id: String?) {

            firestoreRef.document(id.toString())
                .collection(Constants.COLLECTION_PATH_LIVE_MATCH).document("ScoreTeam1")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed", error)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val allTeam1 = value.toObject(Score::class.java)
                        binding.apply {
                            tvFInn.text = value.get("Score").toString().trim()
                            tvOver.text = value.get("Over").toString() + " Over"
                        }
                        //                    val allTeam1 = ArrayList<Score>()
                        //                    allTeam1?.add(allTeam1)
                        Log.d(TAG, "team1: $allTeam1")
                    } else {
                        Log.d(TAG, "No Data")
                    }
                }

            firestoreRef.document(id.toString())
                .collection(Constants.COLLECTION_PATH_LIVE_MATCH).document("ScoreTeam2")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen Failed", error)
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val allTeam2 = value.toObject(Score::class.java)
                        binding.apply {
                            tvSecondFInn.text = value.get("Score").toString().trim()
                            tvSecondOver.text = value.get("Over").toString() + " Over"
                        }
                        Log.d(TAG, "team2: $allTeam2")
                    } else {
                        Log.d(TAG, "No Data")
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardViewHolder {
        val binding =
            LayoutHomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCardViewHolder, position: Int) {
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