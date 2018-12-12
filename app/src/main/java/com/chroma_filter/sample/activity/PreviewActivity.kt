package com.chroma_filter.sample.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.chroma_filter.R
import com.chroma_filter.sample.utils.SendMail
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        supportActionBar!!.hide()
        val imageUri = Uri.parse(intent.getStringExtra("uri"))

        preview_imageview.post { preview_imageview.setImageURI(imageUri) }

        findViewById<View>(R.id.button_gmail_share).setOnClickListener {
            sendViaGmail(imageUri)
           /* val arr = imageUri.toString().split("/")


            val builder = StringBuilder()
            for (i in 0 until arr.size - 1) {
                builder.append(arr[i]+"/")
            }
            val joined = builder.toString()
            val async=object:AsyncTask<Object,Object,Object>()
            {

                override fun doInBackground(vararg p0: Object?): Object {
                    sendMailBySMTP(joined, arr[6]);
                    return "" as Object
                }

            }

            async.execute()*/

        }
        findViewById<View>(R.id.button_whatsapp_share).setOnClickListener {
            sendviaWhatsapp(imageUri)
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


    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }

        return false
    }

    fun sendMailBySMTP(filePath: String, fileName: String) {
        SendMail().sendMessage(filePath, fileName)


    }


}
