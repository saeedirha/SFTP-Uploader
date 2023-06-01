package net.ghiassy.sftp_uploader

import com.jcraft.jsch.JSchException
import net.ghiassy.sftp_uploader.utils.TestServerConnections.testSFTPConnection
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class SFTPConnectionTest {

    @Test
    fun testSFTPConnection_Successful() = runBlocking {
        val host = "10.0.0.117"
        val port = 22
        val username = "test"
        val password = "test"
        val result = testSFTPConnection(host, port, username, password)

        assertEquals(Result.success("SSH connection successful!"), result)
    }

    @Test
    fun testSFTPConnection_Failed() = runBlocking {
        val host = "10.0.0.117"
        val port = 22
        val username = "test"
        val password = "invalid_password"

        val result = testSFTPConnection(host, port, username, password)
        val expectedErrorMessage = "Auth fail"
        val expectedResultString = Result.failure<Exception>(Exception(expectedErrorMessage)).toString()
        val actualResultString = result.toString()

        assertEquals(expectedResultString, actualResultString)
    }

    @Test
    fun testSFTPConnection_Timeout() = runBlocking {
        val host = "10.0.0.5"
        val port = 22
        val username = "your_username"
        val password = "your_password"
        val result = testSFTPConnection(host, port, username, password)

        val expectedErrorMessage = "timeout: socket is not established"
        val expectedResultString = Result.failure<Exception>(Exception(expectedErrorMessage)).toString()
        val actualResultString = result.toString()

        assertEquals(expectedResultString, actualResultString)
    }
}


