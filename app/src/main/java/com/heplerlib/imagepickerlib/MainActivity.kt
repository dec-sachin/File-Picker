package com.heplerlib.imagepickerlib

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.heplerlib.lib.screens.Gallery
import kotlinx.android.synthetic.main.main_activity.*
import android.provider.MediaStore
import android.util.Log

class MainActivity : AppCompatActivity() {

    private var REQUEST_CODE_IMAGES = 111
    private var REQUEST_CODE_VIDEOS = 112
    private var REQUEST_CODE_BOTH = 113
    private var REQUEST_CODE_DOCUMENT = 114

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        btn.setOnClickListener {
            val intent = Intent(this, Gallery::class.java)
            intent.putExtra(Gallery.OPEN_FOR, Gallery.IMAGES)
            startActivityForResult(intent, REQUEST_CODE_IMAGES)
        }

        video.setOnClickListener {
            val intent = Intent(this, Gallery::class.java)
            intent.putExtra(Gallery.OPEN_FOR, Gallery.VIDEOS)
            startActivityForResult(intent, REQUEST_CODE_VIDEOS)
        }

        both.setOnClickListener {
            val intent = Intent(this, Gallery::class.java)
            intent.putExtra(Gallery.OPEN_FOR, Gallery.BOTH)
            intent.putExtra(Gallery.SELECTION_TYPE, Gallery.SINGLE)
            startActivityForResult(intent, REQUEST_CODE_BOTH)
        }

        document.setOnClickListener {
            val intent = Intent(this, Gallery::class.java)
            intent.putExtra(Gallery.OPEN_FOR, Gallery.DOCUMENT)
            startActivityForResult(intent, REQUEST_CODE_DOCUMENT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGES && resultCode == Activity.RESULT_OK) {
            val imagePath = data!!.getStringExtra(Gallery.IMAGE_PATH)
            //image.setImageBitmap(BitmapFactory.decodeFile(imagePath))
            tv_images_path.text = imagePath
        } else if (requestCode == REQUEST_CODE_VIDEOS && resultCode == Activity.RESULT_OK) {
            val videoPath = data!!.getStringExtra(Gallery.VIDEO_PATH)
            tv_images_path.text = videoPath
        } else if (requestCode == REQUEST_CODE_BOTH && resultCode == Activity.RESULT_OK) {
            val imageList = data!!.getStringArrayListExtra(Gallery.PATH_LIST)
            var imagesPath = ""
            for (i in 0 until imageList.size)
                imagesPath += imageList[i] + "\n"

            tv_images_path.text = imagesPath
        } else if (requestCode == REQUEST_CODE_DOCUMENT && resultCode == Activity.RESULT_OK) {
            val path = data!!.getStringExtra(Gallery.DOCUMENT_DATA)
            tv_images_path.text = path
        }
    }

}
