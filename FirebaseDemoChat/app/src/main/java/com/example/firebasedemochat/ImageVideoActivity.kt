package com.example.firebasedemochat

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.android.synthetic.main.activity_image_video.*
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ImageVideoActivity : AppCompatActivity() {
    lateinit var pdfView: PDFView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_video)

        var type = intent.getStringExtra("Type")
        var image = intent.getStringExtra("image")

        if (type == "image") {
            images.visibility = View.VISIBLE
            video.visibility = View.GONE
            idPDFView.visibility = View.GONE
            Glide.with(this)
                .load(image)
                .into(images)
        } else if (type == "pdf"){
            idPDFView.visibility = View.VISIBLE
            RetrievePDFFromURL(pdfView).execute(image)
        } else {
            images.visibility = View.GONE
            video.visibility = View.VISIBLE
            idPDFView.visibility = View.GONE
            video.setVideoURI(image!!.toUri())
            video.start()
        }

//        RetrievePDFFromURL(pdfView).execute(pdfUrl)
    }

    class RetrievePDFFromURL(pdfView: PDFView) :
        AsyncTask<String, Void, InputStream>() {

        // on below line we are creating a variable for our pdf view.
        val mypdfView: PDFView = pdfView

        // on below line we are calling our do in background method.
        override fun doInBackground(vararg params: String?): InputStream? {
            // on below line we are creating a variable for our input stream.
            var inputStream: InputStream? = null
            try {
                val url = URL(params.get(0))
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection

                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                return null;
            }
            return inputStream;
        }
        override fun onPostExecute(result: InputStream?) {
            mypdfView.fromStream(result).load()

        }
    }
}