package com.example.android.politicalpreparedness.election.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.ApiStatus
import com.example.android.politicalpreparedness.election.RetrieveDBStatus
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("formattedDate")
fun bindFormatDate(day: TextView, date: Date?){
    if(date != null){
        day.setText(SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy", Locale.US).format(date))
    }
}

@BindingAdapter("loadingImageForUpcoming")
fun bindLoadingImage(imageView:ImageView, status: ApiStatus){
    when(status){
        ApiStatus.LOADING -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.loading_animation)
        }
        ApiStatus.DONE -> {
            imageView.visibility = View.GONE
        }
        ApiStatus.ERROR -> {
            imageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("loadingImageforSavedElections")
fun bindLoadingImage(imageView:ImageView, status: RetrieveDBStatus){
    when(status){
        RetrieveDBStatus.LOADING -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.loading_animation)
        }
        RetrieveDBStatus.DONE -> {
            imageView.visibility = View.GONE
        }
        RetrieveDBStatus.ERROR -> {
            imageView.visibility = View.GONE
        }
    }
}
