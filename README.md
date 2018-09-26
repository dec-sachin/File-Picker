# Image-Picker

A simple library to select images from the gallery.

## Getting Started

Download the JAR file from JarFile folder and paste it into user app/libs folder in your project. 

### Example

Open Gallery For Selet Multiple Images
```
val intent = Intent(this, Gallery::class.java)
intent.putExtra(Gallery.OPEN_FOR, Gallery.IMAGES)
intent.putExtra(Gallery.SELECTION_TYPE, Gallery.MULTIPLE)
intent.putExtra(Gallery.MAX_SELECTION, 10)
startActivityForResult(intent, REQUEST_CODE_IMAGES)
```
Open Gallery For Selet Single Video
```
val intent = Intent(this, Gallery::class.java)
intent.putExtra(Gallery.OPEN_FOR, Gallery.VIDEOS)
intent.putExtra(Gallery.SELECTION_TYPE, Gallery.SINGLE)
startActivityForResult(intent, REQUEST_CODE_VIDEOS)
```
Open Gallery For Select Both Video and Image
```
val intent = Intent(this, Gallery::class.java)
intent.putExtra(Gallery.OPEN_FOR, Gallery.BOTH)
intent.putExtra(Gallery.SELECTION_TYPE, Gallery.SINGLE)
startActivityForResult(intent, REQUEST_CODE_BOTH)
```
Open Gallery For Select Document
```
val intent = Intent(this, Gallery::class.java)
intent.putExtra(Gallery.OPEN_FOR, Gallery.DOCUMENT)
startActivityForResult(intent, REQUEST_CODE_DOCUMENT)
```

Override onActivityResult method in Your Activity
```
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
  //For Multiple files
  if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      val pathList = data!!.getStringArrayListExtra(Gallery.PATH_LIST)
  }
  //For Single Video Path
  if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      val videoPath = data!!.getStringExtra(Gallery.VIDEO_PATH)
  }
  //For Single Image Path
  if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      val videoPath = data!!.getStringExtra(Gallery.IMAGE_PATH)
  }
  //For Get Document Path
  if (requestCode == REQUEST_CODE_DOCUMENT && resultCode == Activity.RESULT_OK) {
      val path = data!!.getStringExtra(Gallery.DOCUMENT_DATA)
  }
}
```
