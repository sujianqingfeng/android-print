package com.hens.lib.print

import android.content.Context

class  PrintModule(private val context: Context) {
  fun print(){
    println("pring call")
    val renderTask =  PrintPDFRenderTask(context)
    renderTask.render()
  }
}