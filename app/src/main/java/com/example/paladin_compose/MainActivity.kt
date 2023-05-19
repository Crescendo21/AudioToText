package com.example.paladin_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.ui.components.RecordButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.paladin_compose.ui.components.*
import android.Manifest
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.io.File
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.delay
import androidx.compose.runtime.*
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException

class MainActivity : ComponentActivity() {

    private var isRecording by mutableStateOf(false)
    private var isPaused by mutableStateOf(false)
    private var recorder: MediaRecorder? = null
    private var outputFilePath: String? = null
    private var isMediaRecorderRecording by mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar(onSettingsClick = { /* action for settings */ })

                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RecordButton(isRecording, isPaused) {
                                    toggleRecording()
                                }
                                TimerText(isRecording, isPaused)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    ActionButton(
                                        backgroundColor = Color(0xFFFF2E63),
                                        icon = painterResource(id = R.drawable.ic_stop),
                                        text = "Stop",
                                        onClick = {
                                            if (isRecording) {
                                                stopRecording()
                                                requestWriteStoragePermission()
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    ActionButton(
                                        backgroundColor = Color(0xFF08D9D6),
                                        icon = painterResource(id = R.drawable.ic_upload),
                                        text = "Upload",
                                        onClick = { /* Upload action */ }
                                    )
                                }
                            }
                        }

                        CustomButton(text = "Generate") {
                            // action for generate button
                        }

                        CustomBottomNavigationBar(height = 65.dp)
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        startRecordingAfterPermissionGranted()
                    } else {
                        Toast.makeText(this@MainActivity, "Autorisation refusée", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun startRecordingAfterPermissionGranted() {
        val outputFile = getOutputFile()
        outputFile?.let { file ->
            outputFilePath = file.absolutePath
            Log.d("MainActivity", "Output file path: $outputFilePath") // Log ajouté
            recorder = MediaRecorder()
            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(outputFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                try {
                    prepare()
                    start()
                    isRecording = true
                    Toast.makeText(this@MainActivity, "Enregistrement démarré", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Log.e("MainActivity", "prepare() failed", e)
                }
            }
        } ?: run {
            Log.e("MainActivity", "outputFile is null")
        }
    }

    private fun toggleRecording() {
        if (!isRecording) {
            requestPermissions()
        } else {
            if (isPaused) {
                resumeRecording()
            } else {
                pauseRecording()
            }
            isPaused = !isPaused
        }
    }

    private fun pauseRecording() {
        recorder?.apply {
            if (isMediaRecorderRecording) {
                stop()
                isMediaRecorderRecording = false
            }
            reset()
        }
    }

    private fun resumeRecording() {
        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFilePath)
            prepare()
            start()
            isMediaRecorderRecording = true
        }
    }

    private fun stopRecording() {
        if (isRecording || isPaused) {
            try {
                recorder?.apply {
                    stop()
                    reset()
                    release()
                }
            } catch (e: IllegalStateException) {
                // Handle the exception if needed
            }
            recorder = null
            isRecording = false
            isPaused = false
        }
    }


    private fun saveRecording() {
        val audioDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val audioFile = File(audioDirectory, "my_recording.3gp")

        try {
            outputFilePath?.let { sourceFile ->
                val source = File(sourceFile)
                source.copyTo(audioFile, true)
                Toast.makeText(this, "Fichier enregistré dans ${audioFile.path}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur lors de l'enregistrement du fichier", Toast.LENGTH_SHORT).show()
        }
    }



    @Composable
    fun TimerText(isRecording: Boolean, isPaused: Boolean) {
        val elapsedTime = remember { mutableStateOf(0) }

        LaunchedEffect(isRecording, isPaused) {
            if (isRecording && !isPaused) {
                val startTime = System.currentTimeMillis() - elapsedTime.value * 1000
                while (isRecording && !isPaused) {
                    elapsedTime.value = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                    delay(1000)
                }
            }
        }

        val minutes = elapsedTime.value / 60
        val seconds = elapsedTime.value % 60
        val timerText = String.format("%02d:%02d", minutes, seconds)

        Text(
            text = timerText,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }

    private fun requestWriteStoragePermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    saveRecording()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(this@MainActivity, "Autorisation d'écriture sur le stockage refusée", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun getOutputFile(): File? {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        return if (storageDir != null) {
            File(storageDir, "myRecording_${System.currentTimeMillis()}.3gp").apply {
                if (!exists()) {
                    try {
                        createNewFile()
                        Log.d("MainActivity", "Output file created: ${absolutePath}") // Log ajouté
                    } catch (e: IOException) {
                        Log.e("MainActivity", "Failed to create output file", e) // Log ajouté
                    }
                } else {
                    Log.d("MainActivity", "Output file already exists: ${absolutePath}") // Log ajouté
                }
            }
        } else {
            Log.e("MainActivity", "storageDir is null") // Log ajouté
            null
        }
    }


    @Composable
    fun CustomBottomNavigationBar(height: androidx.compose.ui.unit.Dp) {
        CustomNavigationBar(
            containerColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground,
            height = height
        ) {
            BottomNavigationItem(
                icon = { NavIconWithLabel(R.drawable.noun_camera_5673912, "Camera") },
                selected = false,
                onClick = { /* action for audio */ }
            )
            BottomNavigationItem(
                icon = { NavIconWithLabel(R.drawable.microphone_svgrepo_com, "Audio") },
                selected = false,
                onClick = { /* action for camera */ }
            )
            BottomNavigationItem(
                icon = { NavIconWithLabel(R.drawable.upload1_svgrepo_com, "Upload") },
                selected = false,
                onClick = { /* action for upload */ }
            )
        }
    }
}