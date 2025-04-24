package com.anush.dragdrop

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anush.dragdrop.databinding.ItemAddBinding
import com.anush.dragdrop.databinding.ItemImageBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class ImageAdapter(
    private val context: Context,
    private val items: MutableList<ImageItem>,
    private val onAddClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri, showStar: Boolean) {
            Glide.with(context)
                .load(uri)
                .into(binding.imageView)

            binding.starView.visibility = if (showStar) View.VISIBLE else View.GONE
            binding.popMenu.setOnClickListener { showBottomSheet(adapterPosition) }
        }
    }

    inner class FirstImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
            // Using Glide to load the image
            Glide.with(context)
                .load(uri)
                .into(binding.imageView)

            binding.starView.visibility = View.VISIBLE
            binding.popMenu.setOnClickListener { showBottomSheet(adapterPosition) }
        }
    }

    inner class AddViewHolder(private val binding: ItemAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener { onAddClicked() }
        }
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is ImageItem.UriItem -> if (position == 0) 2 else 0
        is ImageItem.AddButton -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> ImageViewHolder(ItemImageBinding.inflate(inflater, parent, false))
            1 -> AddViewHolder(ItemAddBinding.inflate(inflater, parent, false))
            2 -> FirstImageViewHolder(ItemImageBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ImageItem.UriItem -> {
                if (position == 0) (holder as FirstImageViewHolder).bind(item.uri)
                else (holder as ImageViewHolder).bind(item.uri, false)
            }
            is ImageItem.AddButton -> (holder as AddViewHolder).bind()
        }
    }

    override fun getItemCount(): Int = items.size

    fun swapItems(from: Int, to: Int) {
        if (from < items.size - 1 && to < items.size - 1) {
            Collections.swap(items, from, to)
            notifyItemMoved(from, to)
            notifyItemChanged(0)
            if (from == 0 || to == 0) notifyItemChanged(if (from == 0) to else from)
        }
    }

    fun appendImageUris(newUris: List<Uri>) {
        val newItems = newUris.map { ImageItem.UriItem(it) }

        if (items.isEmpty() || items.last() is ImageItem.UriItem) {
            items.add(ImageItem.AddButton)
        }

        val insertPosition = items.size - 1
        items.addAll(insertPosition, newItems)

        notifyDataSetChanged()
    }

    private fun showBottomSheet(position: Int) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_menu, null)
        dialog.setContentView(view)

        view.findViewById<TextView>(R.id.optionMoveForward).setOnClickListener {
            if (position < items.size - 2) {
                Collections.swap(items, position, position + 1)
                notifyItemMoved(position, position + 1)
                notifyItemChanged(0)
            }
            dialog.dismiss()
        }

        view.findViewById<TextView>(R.id.optionMakeCover).setOnClickListener {
            if (position != 0) {
                val item = items.removeAt(position)
                items.add(0, item)
                notifyItemMoved(position, 0)
                notifyItemChanged(0)
                notifyItemChanged(1)
            }
            dialog.dismiss()
        }

        view.findViewById<TextView>(R.id.optionDelete).setOnClickListener {
            items.removeAt(position)

            // If only one item left and it's the AddButton, remove it
            if (items.size == 1 && items[0] is ImageItem.AddButton) {
                items.removeAt(0)
                notifyDataSetChanged()
            } else {
                notifyItemRemoved(position)
                notifyItemChanged(0)
            }

            dialog.dismiss()
        }


        dialog.show()
    }
}
