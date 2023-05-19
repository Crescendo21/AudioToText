import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import java.io.File

class FFmpegPauseResumeRecorder(private val outputFile: File) {
    private var isRecording = false
    private var isPaused = false
    private val pauseFiles = mutableListOf<File>()

    fun startRecording() {
        if (!isRecording) {
            isRecording = true
            isPaused = false
            recordSegment()
        }
    }

    fun pauseRecording() {
        if (isRecording && !isPaused) {
            isPaused = true
            FFmpeg.cancel()
        }
    }

    fun resumeRecording() {
        if (isRecording && isPaused) {
            isPaused = false
            recordSegment()
        }
    }

    fun stopRecording() {
        if (isRecording) {
            if (!isPaused) {
                FFmpeg.cancel()
            }
            isRecording = false
            isPaused = false
            mergeSegments()
        }
    }

    private fun recordSegment() {
        val segmentFile = createTempFile(prefix = "segment_", suffix = ".mp3")
        pauseFiles.add(segmentFile)

        val command = "-y -f android_mic -i audio -vn -c:a libmp3lame -b:a 128k ${segmentFile.absolutePath}"
        FFmpeg.executeAsync(command) { _, returnCode ->
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                if (!isPaused) {
                    recordSegment()
                }
            } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                // Recording was intentionally stopped or paused
            } else {
                // Handle FFmpeg error
            }
        }
    }

    private fun mergeSegments() {
        val concatList = pauseFiles.joinToString(separator = "|") { it.absolutePath }
        val command = "-y -i concat:\"$concatList\" -c copy ${outputFile.absolutePath}"
        FFmpeg.executeAsync(command) { _, returnCode ->
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                // Merging complete
                pauseFiles.forEach { it.delete() }
                pauseFiles.clear()
            } else {
                // Handle FFmpeg error
            }
        }
    }
}
