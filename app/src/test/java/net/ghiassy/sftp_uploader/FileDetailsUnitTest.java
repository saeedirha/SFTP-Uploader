package net.ghiassy.sftp_uploader;

import static org.junit.Assert.assertEquals;

import net.ghiassy.sftp_uploader.utils.FileDetails;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class FileDetailsUnitTest {

    @Test
    public void getHumanReadableFileSize_isCorrect() {

        try{
            File tempFile = File.createTempFile("temp", ".txt");
            tempFile.deleteOnExit();
            int fileSizeBytes = 2 * 1024 * 1024; // 2MB
            fileSizeBytes += 102814;
            byte[] buffer = new byte[1024];
            int remainingBytes = fileSizeBytes;

            Random random = new Random();

            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                while (remainingBytes > 0) {
                    random.nextBytes(buffer);
                    int bytesToWrite = Math.min(buffer.length, remainingBytes);
                    outputStream.write(buffer, 0, bytesToWrite);
                    remainingBytes -= bytesToWrite;
                }
            }catch(IOException e)
            {
                e.printStackTrace();
            }
            String result = FileDetails.getHumanReadableFileSize(tempFile.length()); // 2.10 MB
            assertEquals("2.10 MB", result);


        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
