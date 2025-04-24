package com.anush.dragdrop

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anush.dragdrop.databinding.ItemPagerImageBinding
import com.bumptech.glide.Glide

class ImagePagerAdapter(
    private val context: Context,
    private val imageUris: List<Uri>
) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemPagerImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemPagerImageBinding.inflate(LayoutInflater.from(context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = imageUris[position]
        Glide.with(context)
            .load(uri)
            .into(holder.binding.pagerImageView)
    }

    override fun getItemCount(): Int = imageUris.size
}
