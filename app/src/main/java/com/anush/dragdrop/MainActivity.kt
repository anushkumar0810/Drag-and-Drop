package com.anush.dragdrop

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.anush.dragdrop.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImageAdapter
    private val imageItems = mutableListOf<ImageItem>()
    private val imageUris = mutableSetOf<Uri>() // Use set to avoid duplicates

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            val validNewUris = uris.filterNot { imageUris.contains(it) }
            if (validNewUris.isNotEmpty()) {
                imageUris.addAll(validNewUris)
                adapter.appendImageUris(validNewUris)
            } else {
                Toast.makeText(this, "No new images selected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ImageAdapter(this, imageItems) { openGallery() }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        binding.btnSelectImages.setOnClickListener {
            openGallery()
        }

        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                adapter.swapItems(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerView)

        binding.menu.setOnClickListener {
            showMenuBottomSheet()
        }
    }

    private fun openGallery() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                    galleryLauncher.launch(arrayOf("image/*"))
                } else {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED), 102)
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    galleryLauncher.launch(arrayOf("image/*"))
                } else {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 101)
                }
            }
            else -> {
                galleryLauncher.launch(arrayOf("image/*"))
            }
        }
    }

    private fun showMenuBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_main_menu, null)
        dialog.setContentView(view)

        val addPhotos = view.findViewById<TextView>(R.id.optionAddPhotos)
        val deleteAll = view.findViewById<TextView>(R.id.optionDeleteAll)

        addPhotos.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        deleteAll.setOnClickListener {
            imageItems.clear()
            imageUris.clear()
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((requestCode == 101 || requestCode == 102) && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
