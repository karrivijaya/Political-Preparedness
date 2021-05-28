package com.example.android.politicalpreparedness.representative.adapter

import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.RetrieveDBStatus
import com.example.android.politicalpreparedness.representative.RepresentativeApiStatus

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        val imgUri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
                .load(imgUri)
                .circleCrop()
                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_profile))
                .into(view)
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T>{
    return adapter as ArrayAdapter<T>
}

@BindingAdapter("apiStatus")
fun bindLoadingImage(imageView:ImageView, status: RepresentativeApiStatus?){
    when(status){
        RepresentativeApiStatus.DONE -> {
            imageView.visibility = View.GONE
            Log.d("Binding", "inside done")
        }
        RepresentativeApiStatus.ERROR -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.ic_connection_error)
            Log.d("Binding", "inside error")
        }
        RepresentativeApiStatus.LOADING -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.loading_animation)
            Log.d("Binding", "inside loading")
        }
    }
}
