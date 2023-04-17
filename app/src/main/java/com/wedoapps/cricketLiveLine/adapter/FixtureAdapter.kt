package com.wedoapps.cricketLiveLine.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.StrictMode
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wedoapps.cricketLiveLine.model.Series.MatchModel
import com.squareup.picasso.Picasso
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.FixtureCardItemLayoutBinding
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FixtureAdapter(private val listener: SetOnClick, private val list: ArrayList<MatchModel?>, private val context: Context) :
    RecyclerView.Adapter<FixtureAdapter.FixtureViewHolder>() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private var firestore = FirebaseFirestore.getInstance()
    private val firestoreRef = firestore.collection("MatchList")
    inner class FixtureViewHolder(val binding: FixtureCardItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
      /*  init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = list[position]
                    if (item != null) {
                        listener.onClick(item)
                    }
                }
            }
        }*/

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixtureViewHolder {
        val binding =
            FixtureCardItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FixtureViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: FixtureViewHolder, position: Int) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Log.d(TAG, "onBindViewHolderPOS: $position")
        val currentItem = list[position]
        Log.d(TAG, "onBindViewHolder: ${list.size}")
//        holder.setIsRecyclable(false)
        if (currentItem != null) {
            with(holder){
                with(currentItem) {
                    fun Long?.getDateMatchList(): String {
                        val sdf = SimpleDateFormat("dd MMMM yyyy, h:mm a", Locale.ENGLISH)
                        val cal = Calendar.getInstance(Locale.ENGLISH)
                        cal.timeInMillis = this?.times(1000L) ?: 0L
                        return sdf.format(cal.time)
                    }

                    @SuppressLint("SimpleDateFormat")
                    fun changeHrs(): String {
                        return try {
                            val sdf = SimpleDateFormat("H:mm")
                            val dateObj = this.matchTime?.let { sdf.parse(it) }
                            dateObj?.let { SimpleDateFormat("K:mm a").format(it) } ?: ""
                        } catch (e: ParseException) {
                            e.printStackTrace()
                            ""
                        }
                    }

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = this.matchDate as Long
                    val dayOfWeekString =
                        calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)

                    binding.tvMatchDate.text = "${this.matchDate?.getDateMatchList()}, $dayOfWeekString"

                    binding.tvTypeTime.text =
                        "${this.matchNo} ${this.matchType} - ${changeHrs()}"

                    when (this.matchStatus) {
                        "UPCOMING" -> {
                            binding.tvFInn.isSingleLine = false
                            binding.tvSecondFInn.isSingleLine = false
                            binding.tvOver.visibility = View.GONE
                            binding.tvSecondOver.visibility = View.GONE
                            binding.tvFirstTeam.visibility = View.INVISIBLE
                            binding.tvSecondTeam.visibility = View.INVISIBLE
                            binding.tvFinishedValue.visibility = View.GONE
                            binding.tvUpcomingVenue.visibility = View.VISIBLE
                            binding.tvFInn.gravity = Gravity.CENTER_VERTICAL
                            binding.tvFInn.gravity = Gravity.START
                            binding.tvSecondFInn.gravity = Gravity.CENTER_VERTICAL
                            binding.tvSecondFInn.gravity = Gravity.END
                            binding.tvUpcomingVenue.text = "Venue - ${this.venue?.trim()}"
                            binding.tvMatchStatus.text = "Upcoming"
                            binding.tvMatchStatus.backgroundTintList =
                                ColorStateList.valueOf(itemView.context.getColor(R.color.yellow_orange))
                            binding.tvFInn.text =
                                if (this.team1FullName.isNullOrEmpty()) this.team1Code?.trim() else this.team1FullName!!.trim()
                            binding.tvSecondFInn.text =
                                if (this.team2FullName.isNullOrEmpty()) this.team2Code?.trim() else this.team2FullName!!.trim()
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
                            binding.tvFinishedValue.visibility = View.VISIBLE
                            binding.tvUpcomingVenue.visibility = View.GONE
                            binding.tvFinishedValue.text = this.matchResult?.trim()
                            binding.tvMatchStatus.text = "Finished"
                            binding.tvMatchStatus.backgroundTintList =
                                ColorStateList.valueOf(itemView.context.getColor(R.color.green))
                            binding.tvFirstTeam.text =
                                if (this.team1Code.isNullOrEmpty()) this.team1FullName?.trim() else this.team1Code!!.trim()
                            binding.tvSecondTeam.text =
                                if (this.team2Code.isNullOrEmpty()) this.team2FullName?.trim() else this.team2Code!!.trim()

                            binding.tvFInn.text = this.team1Score
                            binding.tvOver.text = this.team1Over + " Overs"

                            binding.tvSecondFInn.text = this.team2Score
                            binding.tvSecondOver.text = this.team2Over + " Overs"

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
                            binding.tvFinishedValue.visibility = View.GONE
                            binding.tvUpcomingVenue.visibility = View.VISIBLE
                            binding.tvFInn.gravity = Gravity.START
                            binding.tvSecondFInn.gravity = Gravity.END
                            binding.tvUpcomingVenue.gravity = Gravity.START
                            binding.tvMatchStatus.text = "Live"
                            binding.tvUpcomingVenue.text = this.venue?.trim()
                            binding.tvMatchStatus.backgroundTintList =
                                ColorStateList.valueOf(itemView.context.getColor(R.color.colorRed))
                            binding.tvFirstTeam.text =
                                if (this.team1Code.isNullOrEmpty()) this.team1FullName?.trim() else this.team1Code!!.trim()
                            binding.tvSecondTeam.text =
                                if (this.team2Code.isNullOrEmpty()) this.team2FullName?.trim() else this.team2Code!!.trim()

                            binding.tvFInn.text = this.team1Score
                            binding.tvOver.text = this.team1Over + " Overs"

                            binding.tvSecondFInn.text = this.team2Score
                            binding.tvSecondOver.text = this.team2Over + " Overs"
                        }
                        else -> {
                            binding.tvMatchStatus.text = this.matchStatus?.trim()
                        }
                    }

                    storageRef.child("FlagIcon/" + this.team1FullName?.trim { it <= ' ' } + ".png")
                        .downloadUrl.addOnSuccessListener { uri ->
                            Log.d(TAG, "Team1URL: $uri")
//                            holder.binding.ivFirstTeam.setImageURI(uri)
                            Picasso.get()
                                .load(uri)
                                .resize(50,50)
                                .centerCrop()
                                .into(holder.binding.ivFirstTeam)
                          /*  Glide.with(context.applicationContext)
                                .load(uri)
                                .centerCrop()
                                .placeholder(R.drawable.imgpsh_fullsize_anim)
                                .into(holder.binding.ivFirstTeam)*/
                        }.addOnFailureListener {
                            holder.binding.ivFirstTeam.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.imgpsh_fullsize_anim
                                )
                            )
                        }

                    storageRef.child("FlagIcon/" + this.team2FullName?.trim { it <= ' ' } + ".png")
                        .downloadUrl.addOnSuccessListener { uri ->
                            Log.d(TAG, "Team2URL: $uri")
//                            holder.binding.ivFirstTeam.setImageURI(uri)
                            Picasso.get()
                                .load(uri)
                                .resize(50,50)
                                .centerCrop()
                                .into(holder.binding.ivSecondTeam)
                           /* Glide.with(context.applicationContext)
                                .load(uri)
                                .centerCrop()
                                .placeholder(R.drawable.imgpsh_fullsize_anim)
                                .into(holder.binding.ivSecondTeam)*/
                        }.addOnFailureListener {
                            holder.binding.ivSecondTeam.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.imgpsh_fullsize_anim
                                )
                            )
                        }
                }
            }
        }
    }


    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${list.size}")
        return list.size
    }

    interface SetOnClick {
        fun onClick(match: MatchModel)
    }

}