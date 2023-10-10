package net.ghiassy.sftp_uploader.utils;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

public class FileDetails {

    public static String getHumanReadableFileSize(@NonNull long fileSize) {

        long size = fileSize;
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f", size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String getHumanReadableFileSize(@NonNull String filename) throws IOException {

        File file = new File(filename);
        if(!file.exists())
        {
            throw new IOException("File not found!");
        }
        long size = file.length();
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f", size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
