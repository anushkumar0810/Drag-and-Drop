package com.anush.dragdrop

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.anush.dragdrop.databinding.ActivityImageViewBinding
import com.bumptech.glide.Glide

class ImageViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isMultiView = intent.getBooleanExtra("multi_view", false)

        if (isMultiView) {
            setupMultiImageView()
        } else {
            setupSingleImageView()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupSingleImageView() {
        val uri = intent.getParcelableExtra<Uri>("image_uri")
        val index = intent.getIntExtra("image_index", 0)
        val total = intent.getIntExtra("total_count", 1)

        binding.normalImageView.visibility = View.VISIBLE
        binding.scrollImageView.visibility = View.GONE

        Glide.with(this)
            .load(uri)
            .into(binding.imageView)

        binding.textViewCount.text = "Image ${index + 1} of $total"
    }

    private fun setupMultiImageView() {
        val uriStrings = intent.getStringArrayListExtra("uri_list") ?: return
        val startIndex = intent.getIntExtra("start_index", 0)

        binding.scrollImageView.visibility = View.VISIBLE
        binding.normalImageView.visibility = View.GONE

        val uris = uriStrings.map { Uri.parse(it) }
        val pagerAdapter = ImagePagerAdapter(this, uris)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.setCurrentItem(startIndex, false)

        binding.textPagerCount.text = "${startIndex + 1}/${uris.size}"
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.textPagerCount.text = "${position + 1}/${uris.size}"
            }
        })
    }
}
