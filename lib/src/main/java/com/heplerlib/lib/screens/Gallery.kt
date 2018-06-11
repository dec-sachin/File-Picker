package com.heplerlib.lib.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.heplerlib.lib.helper.EqualSpacingItemDecoration
import com.heplerlib.lib.adapters.GalleryAdapter
import com.heplerlib.lib.model.ImageFile
import com.heplerlib.lib.R
import com.heplerlib.lib.adapters.DocumentListAdapter
import com.heplerlib.lib.helper.SimpleDividerItemDecoration
import com.heplerlib.lib.model.DocumentPojo
import kotlinx.android.synthetic.main.gallery_screen.*
import java.io.File
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class Gallery : AppCompatActivity() {

    private val READ_EXTERNAL_REQUEST = 10019

    private var listOfAllFiles = ArrayList<ImageFile>()
    private var listOfAllDocuments = ArrayList<DocumentPojo>()
    private val groupImagesList = ArrayList<ImageFile>()
    private var imageFlag: Boolean = false
    private var videoFlag: Boolean = false
    private var documentFlag: Boolean = false

    private var count = 0
    private var selectedList = ArrayList<String>()

    companion object {
        val IMAGE_PATH = "image_path"
        val VIDEO_PATH = "video_path"
        val DOCUMENT_DATA = "document_path"
        val PATH_LIST = "path_list"

        val SELECTION_TYPE = "selection_type"
        val SINGLE = 0
        val MULTIPLE = 1
        val MAX_SELECTION = "max_selection"

        val OPEN_FOR = "open_for"
        val IMAGES = 0
        val VIDEOS = 1
        val BOTH = 2
        val DOCUMENT = 3

        var selectionType = SINGLE      // Default SelectionType SINGLE
        var maxSelection = 5            // Default Max Selection 5
        var openFor = IMAGES            // Default IMAGES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_screen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        selectionType = intent.getIntExtra(SELECTION_TYPE, SINGLE)
        maxSelection = intent.getIntExtra(MAX_SELECTION, 5)
        openFor = intent.getIntExtra(OPEN_FOR, IMAGES)

        if (openFor == DOCUMENT) {
            supportActionBar?.title = "Documents"
            list_view.layoutManager = LinearLayoutManager(this)
            list_view.addItemDecoration(SimpleDividerItemDecoration(this))
            list_view.adapter = DocumentListAdapter(this, listOfAllDocuments, DocumentListItemClickListener())
        } else {
            when (openFor) {
                IMAGES -> supportActionBar?.title = "Images"
                VIDEOS -> supportActionBar?.title = "Videos"
                else -> supportActionBar?.title = "Gallery"
            }
            list_view.layoutManager = GridLayoutManager(this, 2)
            list_view.addItemDecoration(EqualSpacingItemDecoration(2, 30, true, 0))
            list_view.adapter = GalleryAdapter(this, groupImagesList, ListItemClickListener())
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (askPermission()) {
                loadList()
            }
        } else {
            loadList()
        }
    }

    private fun loadList() {
        if (openFor == IMAGES) {
            supportLoaderManager.initLoader(0, null, ImageLoader())
        } else if (openFor == VIDEOS) {
            supportLoaderManager.initLoader(1, null, VideoLoader())
        } else if (openFor == BOTH) {
            supportLoaderManager.initLoader(0, null, ImageLoader())
            supportLoaderManager.initLoader(1, null, VideoLoader())
        } else if (openFor == DOCUMENT) {
            supportLoaderManager.initLoader(2, null, DocumentLoader())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_menu, menu)
        if (count == 0) {
            menu!!.findItem(R.id.counter).isVisible = false
            menu!!.findItem(R.id.ok).isVisible = false
        } else {
            menu!!.findItem(R.id.counter).title = "[ $count ]"
            menu!!.findItem(R.id.counter).isVisible = true
            menu!!.findItem(R.id.ok).isVisible = true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.ok -> {
                val intent = Intent()
                intent.putStringArrayListExtra(PATH_LIST, selectedList)
                finishScreen(Activity.RESULT_OK, intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onBackPressed() {
        finishScreen(Activity.RESULT_CANCELED)
    }

    private fun finishScreen(resultCode: Int, intent: Intent? = null) {
        if (intent != null)
            setResult(resultCode, intent)
        else
            setResult(resultCode)
        finish()
    }

    private fun askPermission(): Boolean{
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this as Activity, arrayOf(permission), READ_EXTERNAL_REQUEST)
            return false
        } else {
            return true
        }
    }

    private inner class ImageLoader : LoaderManager.LoaderCallbacks<Cursor> {

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            val projection = arrayOf(MediaStore.Images.Media.TITLE, MediaStore.Images.Media._ID, MediaStore.MediaColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            return CursorLoader(this@Gallery, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
            if (imageFlag)
                return
            val listOfAllImages = ArrayList<ImageFile>()
            var absolutePathOfImage: String
            var absoluteIdOfImage: String
            var absoluteTitleOfImage: String
            var absoluteNameOfImage: String
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    absoluteIdOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    absoluteTitleOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE))
                    absoluteNameOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))

                    val imageFile = ImageFile(0, absoluteIdOfImage, absoluteTitleOfImage, absoluteNameOfImage, absolutePathOfImage, 1, 0, 0)
                    listOfAllImages.add(imageFile)
                    listOfAllFiles.add(imageFile)
                }
            }

            Collections.sort(listOfAllImages, kotlin.Comparator { t1, t2 -> return@Comparator t2.groupName.compareTo(t1.groupName) })
            if (listOfAllImages!!.isNotEmpty()) {
                var groupName = ""
                for (item in listOfAllImages!!) {
                    if (item.groupName != groupName) {
                        //Log.e("testing", "ITEM : ${item.id} >>> ${item.title} >>> ${item.groupName}")
                        groupImagesList.add(item)
                        groupName = item.groupName
                    } else {
                        groupImagesList[groupImagesList.size-1].counter++
                    }
                }
            }

            list_view.adapter.notifyDataSetChanged()
            imageFlag = true
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {}
    }

    private inner class VideoLoader : LoaderManager.LoaderCallbacks<Cursor> {

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            val projection = arrayOf(MediaStore.Video.Media.TITLE, MediaStore.Video.Media._ID, MediaStore.MediaColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            return CursorLoader(this@Gallery, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
            if (videoFlag)
                return
            val listOfAllVideos = ArrayList<ImageFile>()
            var absolutePathOfImage: String
            var absoluteIdOfImage: String
            var absoluteTitleOfImage: String
            var absoluteNameOfImage: String
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    absoluteIdOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    absoluteTitleOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE))
                    absoluteNameOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))

                    val imageFile = ImageFile(0, absoluteIdOfImage, absoluteTitleOfImage, absoluteNameOfImage, absolutePathOfImage, 1, 1, 0)
                    listOfAllVideos!!.add(imageFile)
                    listOfAllFiles.add(imageFile)
                }
            }

            Collections.sort(listOfAllVideos, kotlin.Comparator { t1, t2 -> return@Comparator t2.groupName.compareTo(t1.groupName) })
            if (listOfAllVideos!!.isNotEmpty()) {
                var groupName = ""
                for (item in listOfAllVideos!!) {
                    if (item.groupName != groupName) {
                        groupImagesList.add(item)
                        groupName = item.groupName
                    } else {
                        groupImagesList[groupImagesList.size-1].counter++
                    }
                }
            }

            list_view.adapter.notifyDataSetChanged()
            videoFlag = true
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {}
    }

    private inner class DocumentLoader : LoaderManager.LoaderCallbacks<Cursor> {

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA)
            val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE)
            return CursorLoader(this@Gallery, MediaStore.Files.getContentUri("external"), projection, selection, null, null)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
            if (documentFlag)
                return
            var documentId: String
            var documentPath: String
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    documentId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    documentPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))

                    if (documentPath.contains(".pdf") or documentPath.contains(".txt") or documentPath.contains(".zip") or
                            documentPath.contains(".docx") or documentPath.contains(".apk")) {
                        listOfAllDocuments!!.add(DocumentPojo(documentId, File(documentPath).name, documentPath, getFileSize(File(documentPath).length()), false))
                    }
                }
            }
            list_view.adapter.notifyDataSetChanged()
            documentFlag = true
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == READ_EXTERNAL_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadList()
            } else {
                onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AllImagesScreen.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (!data!!.getBooleanExtra("finish", false)) {
                val selectedFile = data!!.getParcelableExtra<ImageFile>("file")
                val tempList = data!!.getParcelableArrayListExtra<ImageFile>("temp_list")
                var selectionCount = 0
                for (imageFile in tempList) {
                    if (imageFile.checked == 1)
                        selectionCount++
                    listOfAllFiles[imageFile.index] = imageFile
                }
                updateCountView()
                var groupIndex = 0
                for ((index, groupImage) in groupImagesList.withIndex()) {
                    if (groupImage.groupName == selectedFile.groupName) {
                        groupImage.selectionCount = selectionCount
                        groupIndex = index
                        break
                    }
                }
                list_view.adapter.notifyItemChanged(groupIndex)
            } else {
                finishScreen(Activity.RESULT_OK, data!!)
            }
        }
    }

    private fun updateCountView() {
        selectedList.clear()
        for (imageFile in listOfAllFiles) {
            if (imageFile.checked == 1)
                selectedList.add(imageFile.path)
        }
        count = selectedList.size
        invalidateOptionsMenu()
    }

    private inner class ListItemClickListener: GalleryAdapter.ItemClickListener {
        override fun onItemClick(imageFile: ImageFile) {
            val list = ArrayList<ImageFile>()
            for ((i, item) in listOfAllFiles!!.withIndex()) {
                if (item.groupName == imageFile.groupName) {
                    item.index = i
                    list.add(item)
                }
            }
            val bundle = Bundle()
            bundle.putParcelable("file", imageFile)
            bundle.putParcelableArrayList("list", list)
            bundle.putInt("total_selected", count)
            val intent  = Intent(this@Gallery, AllImagesScreen::class.java)
            intent.putExtras(bundle)
            startActivityForResult(intent, AllImagesScreen.REQUEST_CODE)
        }
    }

    private inner class DocumentListItemClickListener: DocumentListAdapter.ItemClickListener {
        override fun onItemClick(documentPojo: DocumentPojo) {
            val intent = Intent()
            intent.putExtra(Gallery.DOCUMENT_DATA, documentPojo.path)
            finishScreen(Activity.RESULT_OK, intent)
        }
    }

    fun getFileSize(size: Long): String {
        if (size <= 0)
            return "0B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

}
