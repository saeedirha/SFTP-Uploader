import kotlinx.coroutines.runBlocking
import net.ghiassy.sftp_uploader.utils.TestServerConnections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FTPConnectionTest {

    @Test
    fun testFTPConnection_Successful() = runBlocking {
        val host = "10.0.0.58"
        val port = 2121
        val username = "admin"
        val password = "admin"

        val result = TestServerConnections.testFTPConnection(host, port, username, password)

        assertTrue(result.isSuccess)
        assertEquals("Connection was successful", result.getOrNull())
    }

    @Test
    fun testFTPConnection_Failed() = runBlocking {
        val host = "10.0.0.58"
        val port = 21
        val username = "invalid-username"
        val password = "invalid-password"

        val result = TestServerConnections.testFTPConnection(host, port, username, password)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() != null)
    }
}
