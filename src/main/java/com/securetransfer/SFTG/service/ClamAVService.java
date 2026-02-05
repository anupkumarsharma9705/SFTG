package com.securetransfer.SFTG.service;

import com.securetransfer.SFTG.exception.FileStorageException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

@Service
public class ClamAVService {

    // ClamAV daemon default socket (Linux)
    private static final String CLAMAV_HOST = "localhost";
    private static final int CLAMAV_PORT = 3310;

    public void scanFile(Path filePath) {
        try (Socket socket = new Socket(CLAMAV_HOST, CLAMAV_PORT);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            // INSTREAM command
            out.write("zINSTREAM\0".getBytes());
            out.flush();

            try (InputStream fis = new FileInputStream(filePath.toFile())) {
                byte[] buffer = new byte[2048];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    out.write(new byte[]{
                            (byte) (read >> 24),
                            (byte) (read >> 16),
                            (byte) (read >> 8),
                            (byte) read
                    });
                    out.write(buffer, 0, read);
                }
            }

            // End of stream
            out.write(new byte[]{0, 0, 0, 0});
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String response = reader.readLine();

            if (response != null && response.contains("FOUND")) {
                throw new FileStorageException("Malicious file detected by antivirus");
            }

        } catch (IOException e) {
            throw new FileStorageException("Virus scan failed. Upload rejected.", e);
        }
    }
}
