package com.hens.lib.print

import android.annotation.SuppressLint
import android.content.Context
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentAdapter.LayoutResultCallback
import android.print.PrintDocumentAdapter.WriteResultCallback
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.UiThread
import kotlin.math.roundToInt


class PrintPDFRenderTask(private val context: Context) {

  private lateinit var webView: WebView
  private val PIXELS_PER_INCH = 72
  private val MILS_PER_INCH = 1000.0
  private val PIXELS_PER_MIL = PIXELS_PER_INCH / MILS_PER_INCH
  private val DEFAULT_MEDIA_WIDTH = 612
  private val DEFAULT_MEDIA_HEIGHT = 792
  private var fileDescriptor: ParcelFileDescriptor? = null
  private lateinit var document: PrintDocumentAdapter
  private var numberOfPages = 0

  @UiThread
  fun render(){
    println("render call1")
    webView = WebView(context)

    val settings = webView.settings
    settings.defaultTextEncodingName = "UTF-8"

    webView.webViewClient = webViewClient

    val html = "<h1>hemtl fdfdf</h1>"
    webView.loadDataWithBaseURL(null, html, "text/html; charset=utf-8", "UTF-8", null)

  }


  private val printAttributes: PrintAttributes
    get() {
      val width = DEFAULT_MEDIA_WIDTH
      val height = DEFAULT_MEDIA_HEIGHT
      val builder = PrintAttributes.Builder()
      val mediaSize = PrintAttributes.MediaSize(
        "id",
        "label",
        (width / PIXELS_PER_MIL).roundToInt(),
        (height / PIXELS_PER_MIL).roundToInt()
      )
      builder
        .setMediaSize(mediaSize)
        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        .setResolution(PrintAttributes.Resolution("id", "label", PIXELS_PER_INCH, PIXELS_PER_INCH))
      return builder.build()
    }

  private  val webViewClient: WebViewClient = object : WebViewClient(){
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
      println("shouldOverrideUrlLoading")
      return false
    }

    override fun onPageFinished(view: WebView, url: String?) {
      println("page finished")

      document = view.createPrintDocumentAdapter("Document")
      // layout the document with appropriate print attributes
      document.onLayout(null, printAttributes, null, object : LayoutResultCallback() {

      }, null)
      @SuppressLint("Range")
      val pageHeight = PIXELS_PER_MIL * printAttributes.mediaSize!!.heightMils
      numberOfPages = 1 + (view.contentHeight / pageHeight).toInt()

      // Write to a file if file path was passed, otherwise invoke onRenderFinish callback
      if (fileDescriptor != null) {
        document.onWrite(arrayOf(PageRange.ALL_PAGES), fileDescriptor, null, object: WriteResultCallback(){

        })
      } else {
      }
    }
  }
}