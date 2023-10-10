package net.ghiassy.sftp_uploader.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.jcraft.jsch.SftpProgressMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ghiassy.sftp_uploader.models.UploadProgressShareModel
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class SFTPUploader(
    private val context: Context,
    private val hostname: String,
    private val port: Int,
    private val username: String,
    private val password: String,
    private val fileName: String,
    private val fileSize: Long,
    private val fileInputStream: java.io.InputStream
) {

    private val TAG = "FTPUploader"

    var countBytes = 0L
    var percent = 0

    suspend fun uploadFile(
        activity: Activity,
        uploadProgressShareModel: UploadProgressShareModel
    ) {
        withContext(Dispatchers.IO) {
            try {

                val jsch = JSch()
                val session: Session = jsch.getSession(username, hostname, port)
                session.setPassword(password)
                // Avoid asking for key confirmation
                session.setConfig("StrictHostKeyChecking", "no")

                session.connect(8000)

                val channel = session.openChannel("sftp")
                channel.connect()
                val channelSftp = channel as ChannelSftp

                val progress: SftpProgressMonitor = object : SftpProgressMonitor {
                    override fun init(op: Int, src: String, dest: String, max: Long) {
                        activity.runOnUiThread {
                            Log.d(TAG, "Starting upload of $dest")
                        }
                    }

                    override fun count(i: Long): Boolean {
                        countBytes += i
                        percent = (countBytes * 100 / fileSize).toInt()
                        activity.runOnUiThread {
                            uploadProgressShareModel.updateProgress(percent)
                            //Log.d(TAG, "Uploaded $percent%")
                        }
                        return true
                    }

                    override fun end() {
                        activity.runOnUiThread {
                            Log.d(TAG, "Upload finished")
                            Log.d(TAG, "Uploaded $countBytes bytes")
                            Log.d(TAG, "File size: $fileSize")
                            Log.d(TAG,"Percent: $percent")
                        }
                    }
                }

                channelSftp.put(fileInputStream, fileName, progress)
                channelSftp.exit()
                session.disconnect()
                activity.runOnUiThread {
                    Snackbar.make(
                        activity.findViewById(android.R.id.content),
                        "Upload was successful :)",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            } catch (e: JSchException) {
                e.printStackTrace()
                activity.runOnUiThread {
                    Toast.makeText(context, "Fatal error: ${e.message}", Toast.LENGTH_LONG).show()
                    uploadProgressShareModel.error()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                activity.runOnUiThread {
                    Toast.makeText(context, "Fatal error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
