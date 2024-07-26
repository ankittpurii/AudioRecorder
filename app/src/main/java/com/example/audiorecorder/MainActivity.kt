package com.example.audiorecorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.thezone.audiorecorder.Timer
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

val REQUEST_CODE=200
class MainActivity : AppCompatActivity(), Timer.OnTimerTickListener{

    private var permissions= arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted= false
    private lateinit var recorder: MediaRecorder
    private var dirPath=""
    private var fileName=""
    private lateinit var  recordButton :ImageButton
    private lateinit var  timerText :TextView
    private lateinit var timer: Timer

    private var isRecording = false
    private var isPaused = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recordButton= findViewById<ImageButton>(R.id.recordButton)
        timerText= findViewById<TextView>(R.id.timer)
        permissionGranted= ActivityCompat.checkSelfPermission(this, permissions[0])==PackageManager.PERMISSION_GRANTED
        if(!permissionGranted)
        {
            requestRecording()
        }

        timer=Timer(this )
        recordButton.setOnClickListener{
            when{
                isPaused -> resumeRecording()
                isRecording -> pauseRecorder()
                else -> startRecording()
        }
        }

    }

    private fun resumeRecording(){
        recorder.resume()
        isPaused=false
        timer.start()
        recordButton.setImageResource(R.drawable.ic_record)
    }
    private fun pauseRecorder(){
        recorder.pause()
        isPaused=true
        timer.pause()
        recordButton.setImageResource(R.drawable.ic_pause)
    }

    private fun stopRecorder(){
            timer.stop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== REQUEST_CODE)
        {
            permissionGranted=grantResults[0]==PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestRecording(){
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording(){
        if(!permissionGranted)
        {
            requestRecording()
            return
        }

        recorder= MediaRecorder(this);
        dirPath="${externalCacheDir?.absolutePath}/"
        var simpleDateFormat=SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        fileName="audio_record_$date"
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$fileName.mp3")

            try {
                prepare()
            }catch (e : IOException){}
            start()
            timer.start()
            recordButton.setImageResource(R.drawable.ic_pause)
            isRecording=true
            isPaused=false
        }
    }

    override fun onTimerTick(duration: String) {
        timerText.setText(duration)
    }
}