package net.ghiassy.sftp_uploader.utils

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply

object TestServerConnections {

    private val TAG = "TestServerConnections"

    suspend fun testFTPConnection(
        host: String,
        port: Int,
        username: String,
        password: String
    ): Result<String> {
        val ftpClient = FTPClient()

        return try {
            ftpClient.connect(host, port)
            val replyCode = ftpClient.replyCode
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw Exception("Authentication failed. Reply code: $replyCode")
            }

            ftpClient.login(username, password)
            if (!FTPReply.isPositiveCompletion(ftpClient.replyCode)) {
                throw Exception("Authentication failed. Reply code: ${ftpClient.replyCode}")
            }
            ftpClient.enterLocalPassiveMode()
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

            Result.success("Connection was successful")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception(e.message))
        } finally {
            ftpClient.disconnect()
        }
    }


    suspend fun testSFTPConnection(
        host: String,
        port: Int,
        username: String,
        password: String
    ): Result<String> {
        return try {
            val jsch = JSch()
            val session: Session = jsch.getSession(username, host, port)
            session.setPassword(password)
            // Avoid asking for key confirmation
            session.setConfig("StrictHostKeyChecking", "no")

            session.connect(8000)
            // Test the connection
            val result = if (session.isConnected) {
                Result.success("SSH connection successful!")
            } else {
                Result.failure(Exception("Failed to establish SSH connection."))
            }

            // Disconnect from the SSH server
            session.disconnect()

            result
        } catch (e: JSchException) {
            Result.failure(Exception(e.message))
        }
    }

}