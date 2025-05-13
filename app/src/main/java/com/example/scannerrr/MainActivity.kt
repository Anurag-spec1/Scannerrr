package com.example.scannerrr


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*

class MainActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {
    private lateinit var codeScanner: CodeScanner
    private lateinit var scanButton: ImageView
    private lateinit var textView: TextView
    private lateinit var textView1: TextView
    private lateinit var scannerView: CodeScannerView
    private lateinit var videoTexture: TextureView
    private var mediaPlayer: MediaPlayer? = null
    private val CAMERA_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanButton = findViewById(R.id.scanbtn)
        scannerView = findViewById(R.id.scanner_view)
        textView = findViewById(R.id.constant)
        textView1 = findViewById(R.id.constant2)
        videoTexture = findViewById(R.id.videoTexture)

        videoTexture.surfaceTextureListener = this


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            setupScanner()
        }


        scanButton.setOnClickListener {
            scannerView.visibility = View.VISIBLE
            codeScanner.startPreview()
            scanButton.visibility = View.GONE
            textView.visibility = View.GONE
            textView1.visibility = View.VISIBLE
        }
    }


    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.vdoplbck}")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@MainActivity, videoUri)
            setSurface(Surface(surface))
            isLooping = true
            setOnPreparedListener {
                scaleVideo(width, height)
                start()
            }
            prepareAsync()
        }
    }

    private fun scaleVideo(screenWidth: Int, screenHeight: Int) {
        mediaPlayer?.let { mp ->
            val videoWidth = mp.videoWidth
            val videoHeight = mp.videoHeight
            val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
            val screenRatio = screenWidth.toFloat() / screenHeight.toFloat()

            if (videoRatio > screenRatio) {
                videoTexture.scaleX = videoRatio / screenRatio
            } else {
                videoTexture.scaleY = screenRatio / videoRatio
            }
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mediaPlayer?.release()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupScanner()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupScanner() {
        codeScanner = CodeScanner(this, scannerView)


        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false


        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val intent = Intent(this, Result::class.java)
                intent.putExtra("key", it.text)
                startActivity(intent)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }


        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
        if (::codeScanner.isInitialized && scannerView.visibility == View.VISIBLE) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        mediaPlayer?.pause()
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}


