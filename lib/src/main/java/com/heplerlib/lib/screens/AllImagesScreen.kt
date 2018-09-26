package com.heplerlib.lib.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.heplerlib.lib.adapters.AllImagesAdapter
import com.heplerlib.lib.helper.EqualSpacingItemDecoration
import com.heplerlib.lib.model.ImageFile
import com.heplerlib.lib.R
import kotlinx.android.synthetic.main.all_images_screen.*

class AllImagesScreen : AppCompatActivity() {

    private lateinit var imagesList: ArrayList<ImageFile>
    private var count = 0
    private var totalSelected = 0
    private lateinit var selectedFile: ImageFile

    companion object {
        val REQUEST_CODE = 115
        fun start(context: Context, bundle: Bundle) {
            val intent = Intent(context, AllImagesScreen::class.java)
            intent.putExtras(bundle)
            (context as Activity).startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_images_screen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        selectedFile = intent.extras.getParcelable<ImageFile>("file")
        imagesList = Gallery.listToForward!!
        totalSelected = intent.extras.getInt("total_selected")
        supportActionBar?.title = selectedFile.groupName

        list_view.layoutManager = GridLayoutManager(this, 3)
        list_view.addItemDecoration(EqualSpacingItemDecoration(3, 20, true, 0))
        list_view.adapter = AllImagesAdapter(this, imagesList, ListItemClickListener(), totalSelected)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.all_menu, menu)
        if (count == 0) {
            menu!!.findItem(R.id.counter).isVisible = false
            menu!!.findItem(R.id.ok_btn).isVisible = false
        } else {
            menu!!.findItem(R.id.counter).title = "[ $count ]"
            menu!!.findItem(R.id.counter).isVisible = true
            menu!!.findItem(R.id.ok_btn).isVisible = true
        }
        return super.onCreateOptionsMenu(menu)
    }

    fun updateMenu(count: Int) {
        this.count = count
        invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.ok_btn -> {
                val intent = Intent()
                val list = ArrayList<String>()
                for (image in imagesList) if (image.checked == 1) list.add(image.path)
                intent.putStringArrayListExtra(Gallery.PATH_LIST, list)
                intent.putExtra("finish", true)
                finishScreen(Activity.RESULT_OK, intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("file", selectedFile)
        intent.putParcelableArrayListExtra("temp_list", imagesList)
        intent.putExtra("finish", false)
        finishScreen(Activity.RESULT_OK, intent)
    }

    private fun finishScreen(resultCode: Int, intent: Intent? = null) {
        if (intent != null)
            setResult(resultCode, intent)
        else
            setResult(resultCode)
        finish()
    }

    private inner class ListItemClickListener: AllImagesAdapter.ItemClickListener {
        override fun onItemClick(imageFile: ImageFile) {
            val intent = Intent()
            val list = ArrayList<String>()
            list.add(imageFile.path)
            if (Gallery.openFor == Gallery.IMAGES)
                intent.putExtra(Gallery.IMAGE_PATH, imageFile.path)
            else
                intent.putExtra(Gallery.VIDEO_PATH, imageFile.path)
            intent.putStringArrayListExtra(Gallery.PATH_LIST, list)
            intent.putExtra("finish", true)
            finishScreen(Activity.RESULT_OK, intent)
        }
    }

}
