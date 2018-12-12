/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage.sample.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast

import com.chroma_filter.R
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageChromaKeyBlendFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.FilterAdjuster
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.createFilterForChroma
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools.createFilterForType

import jp.co.cyberagent.android.gpuimage.sample.utils.Camera1Loader
import jp.co.cyberagent.android.gpuimage.sample.utils.Camera2Loader
import jp.co.cyberagent.android.gpuimage.sample.utils.CameraLoader
import jp.co.cyberagent.android.gpuimage.sample.utils.doOnLayout
import jp.co.cyberagent.android.gpuimage.util.Rotation
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.CountDownTimer
import android.provider.MediaStore
import android.support.v7.appcompat.R.attr.height
import android.widget.AdapterView
import android.widget.TextView
import com.chroma_filter.sample.activity.PreviewActivity
import kotlinx.android.synthetic.main.activity_camera.*


class CameraActivity : AppCompatActivity() {
    private var timer = "0"
    private val gpuImageView: GPUImageView by lazy { findViewById<GPUImageView>(R.id.surfaceView) }
    private val seekBar: SeekBar by lazy { findViewById<SeekBar>(R.id.seekBar) }
    private val cameraLoader: CameraLoader by lazy {
        if (Build.VERSION.SDK_INT < 21) {
            Camera1Loader(this)
        } else {
            Camera2Loader(this)
        }
    }
    private var filterAdjuster: FilterAdjuster? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                filterAdjuster?.adjust(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        findViewById<View>(R.id.button_choose_filter).setOnClickListener {
            // switchFilterTo(GPUImageFilter.)
            switchFilterTo(createFilterForChroma(this, R.drawable.filter_one))
            /* GPUImageFilterTools.showDialog(this) {
                     filter -> switchFilterTo(filter)
             }*/
        }

        findViewById<View>(R.id.button_choose_frame).setOnClickListener {
           // switchFilterTo(createFilterForChroma(this, R.drawable.filter_one))
            frame_img.visibility = View.VISIBLE
        }


        findViewById<View>(R.id.button_whatsapp_share).setOnClickListener {
           capturePic("whatsapp")
        }
        findViewById<View>(R.id.button_choose_filter_two).setOnClickListener {
            switchFilterTo(createFilterForChroma(this, R.drawable.filter_two))

        }
        findViewById<View>(R.id.button_choose_filter_three).setOnClickListener {
            switchFilterTo(createFilterForChroma(this, R.drawable.filter_three))
        }

        findViewById<View>(R.id.button_choose_filter_four).setOnClickListener {
            switchFilterTo(createFilterForChroma(this, R.drawable.filter_four))
        }
        findViewById<View>(R.id.button_choose_filter_five).setOnClickListener {
            switchFilterTo(createFilterForChroma(this, R.drawable.filter_five))
        }

        findViewById<View>(R.id.button_capture).setOnClickListener {
            capturePic("")
        }
        findViewById<View>(R.id.button_gmail_share).setOnClickListener {
            capturePic("gmail")
        }
        findViewById<View>(R.id.img_switch_camera).run {
            if (!cameraLoader.hasMultipleCamera()) {
                visibility = View.GONE
            }
            setOnClickListener {
                cameraLoader.switchCamera()
                gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
            }
        }

        counter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, p2: Int, p3: Long) {
                if (view != null)
                    timer = (view as TextView).text.toString().split(" ")[0]

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        cameraLoader.setOnPreviewFrameListener { data, width, height ->
            gpuImageView.updatePreviewFrame(data, width, height)
        }
      //  gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
        gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
        gpuImageView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY)
    }

    override fun onResume() {
        super.onResume()
        gpuImageView.doOnLayout {
            cameraLoader.onResume(it.width, it.height)
        }
    }

    override fun onPause() {
        cameraLoader.onPause()
        super.onPause()
    }

    fun  capturePic(shareVia: String) {
        if (timer.equals("10")) {
            val cdt = object : CountDownTimer(10 * 1000, 1000) {
                override fun onFinish() {
                    tv_counter.visibility = View.GONE
                    saveSnapshot(shareVia)
                }

                override fun onTick(p0: Long) {
                    tv_counter.visibility = View.VISIBLE
                    val time = (p0 / 1000)
                    if (time == 0.toLong()) {
                        tv_counter.text = "Ready"
                    } else {
                        tv_counter.text = (p0 / 1000).toString()
                    }

                }
            }
            cdt.start()

        } else {
            saveSnapshot(shareVia)
        }
    }

    private fun saveSnapshot(shareVia: String) {

        val folderName = "GPUImage"
        val fileName = System.currentTimeMillis().toString() + ".jpg"

        gpuImageView.saveToPictures(folderName, fileName, object : GPUImageView.OnPictureSavedListener {
            override fun onPictureSaved(uri: Uri) {
               
                if (shareVia.equals("whatsapp")) {

                    sendviaWhatsapp(uri)
                } else if (shareVia.equals("gmail")) {
                    sendViaGmail(uri)
                } else {
                    Toast.makeText(applicationContext, uri.toString() + " saved", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, PreviewActivity::class.java)
                    intent.putExtra("uri", uri.toString())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    applicationContext.startActivity(intent)
                }

            }
        })

    }

    fun Output(takenPhoto: Bitmap, frameImage: Bitmap): Bitmap {
        val width: Int
        val height: Int
        val x: Int
        val y: Int
        height = 100 // your desired height of the whole output image
        width = 100 // your desired width of the whole output image
        x = 200 // specify indentation for the picture
        y = 200

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(result)
        val paint = Paint()

        c.drawBitmap(takenPhoto, x.toFloat(), y.toFloat(), paint) // draw your photo over canvas, keep indentation in mind (x and y)
        c.drawBitmap(frameImage, 0f, 0f, paint) // now draw your frame on top of the image

        return result
    }

    private fun getRotation(orientation: Int): Rotation {
        return when (orientation) {
            90 -> Rotation.ROTATION_90
            180 -> Rotation.ROTATION_180
            270 -> Rotation.ROTATION_270
            else -> Rotation.NORMAL
        }
    }

    private fun switchFilterTo(filter: GPUImageFilter) {
        //  if (gpuImageView.filter == null || gpuImageView.filter!!.javaClass != filter.javaClass) {
        gpuImageView.filter = filter
        filterAdjuster = FilterAdjuster(filter)
        filterAdjuster?.adjust(seekBar.progress)
        //  }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }

        return false
    }


    fun sendViaGmail(uri: Uri) {
        if (appInstalledOrNot("com.google.android.gm")) {
            val intent = Intent(Intent.ACTION_SEND)
            val recipients = arrayOf("mailto@gmail.com")
            intent.putExtra(Intent.EXTRA_EMAIL, recipients)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject text here...")
            intent.putExtra(Intent.EXTRA_TEXT, "Body of the content here...")
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "image/*"
            intent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(intent, "Send mail"))

        } else {

            Toast.makeText(applicationContext, "Gmail Not Installed...", Toast.LENGTH_SHORT).show()
        }


    }


    fun sendviaWhatsapp(uri: Uri) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
        sendIntent.type = "image/*"
        if (appInstalledOrNot("com.whatsapp")) {
            sendIntent.setPackage("com.whatsapp")
            startActivity(sendIntent)
        } else if (appInstalledOrNot("com.whatsapp.w4b")) {
            sendIntent.setPackage("com.whatsapp.w4b")
            startActivity(sendIntent)
        } else {
            Toast.makeText(applicationContext, "Whatsapp is not istalled...", Toast.LENGTH_SHORT).show()
        }

    }

}
