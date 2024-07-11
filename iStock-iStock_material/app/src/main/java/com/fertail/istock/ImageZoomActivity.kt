package com.fertail.istock

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import com.fertail.istock.databinding.ActivityZoomBinding
import com.fertail.istock.ui.dataclass.CustomInterface
import com.squareup.picasso.Picasso
import java.io.File
import kotlin.math.max
import kotlin.math.min


class ImageZoomActivity : BaseActivity()  {

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private lateinit var binding: ActivityZoomBinding

    var builder: AlertDialog.Builder? = null


    companion object {
        var customInterface : CustomInterface? = null
        var position : Int? = null
        var mUri : Uri? = null
        var mUrl : String? = null

        fun start(caller: Context, param: CustomInterface, i: Int, url: String?){
            val intent = Intent(caller, ImageZoomActivity::class.java)
            customInterface = param
            position =  i
            mUrl = url
            caller.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

        if (mUrl!!.contains("http")){
            Picasso.get().load(mUrl).into(binding.imageView);
        }else{
            Picasso.get().load(File(mUrl)).into(binding.imageView);
        }

        binding.delete.setOnClickListener {
            builder = AlertDialog.Builder(this)

            builder!!.setMessage("Do you want to delete this image ?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    customInterface!!.itemNeedToDelete(position!!)
                    finish()
                }
                .setNegativeButton("No") { dialog, id -> //  Action for 'NO' Button
                    dialog.cancel()
                }
            //Creating dialog box
            //Creating dialog box
            val alert = builder!!.create()
            //Setting the title manually
            //Setting the title manually
            alert.setTitle(resources.getString(R.string.app_name))
            alert.show()


        }

        binding.imgClose.setOnClickListener {
            finish()
        }
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(motionEvent)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 10.0f))
            binding.imageView.scaleX = scaleFactor
            binding.imageView.scaleY = scaleFactor
            return true
        }
    }
}