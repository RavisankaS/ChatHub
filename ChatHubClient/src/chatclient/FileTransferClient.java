package chatclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileTransferClient {
	
	// port used by the server for file transfer
    private static final int FILE_PORT = 5001;

    private final String serverAddress;

    public FileTransferClient(
            String serverAddress) {

        this.serverAddress =
                serverAddress;
    }

    // send a file to online user
    public boolean sendFile(
            File file,
            String sender,
            String recipient)
            throws IOException {
    	
    	// check file validity
        if (file == null
                || !file.exists()
                || !file.isFile()) {

            return false;
        }

        try (
                Socket socket =
                        new Socket(
                                serverAddress,
                                FILE_PORT
                        );

                DataOutputStream output =
                        new DataOutputStream(
                                new BufferedOutputStream(
                                        socket.getOutputStream()
                                )
                        );

                DataInputStream input =
                        new DataInputStream(
                                new BufferedInputStream(
                                        socket.getInputStream()
                                )
                        );

                FileInputStream fileInput =
                        new FileInputStream(file)
        ) {
        	
        	// tell server that this connection is for uploading
            output.writeUTF(
                    "UPLOAD"
            );
            // sending sender and receiver info
            output.writeUTF(
                    sender
            );

            output.writeUTF(
                    recipient
            );
            // send the file name and size
            output.writeUTF(
                    file.getName()
            );

            output.writeLong(
                    file.length()
            );
            // read the file before sending it
            byte[] buffer =
                    new byte[8192];

            int bytesRead;

            while (
                    (bytesRead =
                            fileInput.read(buffer))
                            != -1
            ) {

                output.write(
                        buffer,
                        0,
                        bytesRead
                );
            }
            // making sure file data is sent
            output.flush();
            // waiting for server to confirm
            String response =
                    input.readUTF();

            return response.equals(
                    "UPLOAD_OK"
            );
        }
    }

    // file download
    public File downloadFile(
            String username,
            String fileName)
            throws IOException {
    	// saving files in a folder
        File directory =
                new File(
                        "received_files"
                );

        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (
                Socket socket =
                        new Socket(
                                serverAddress,
                                FILE_PORT
                        );

                DataOutputStream output =
                        new DataOutputStream(
                                new BufferedOutputStream(
                                        socket.getOutputStream()
                                )
                        );

                DataInputStream input =
                        new DataInputStream(
                                new BufferedInputStream(
                                        socket.getInputStream()
                                )
                        )
        ) {
        	// telling server that we want to download a file
            output.writeUTF(
                    "DOWNLOAD"
            );

            output.writeUTF(
                    username
            );

            output.writeUTF(
                    fileName
            );

            output.flush();
            // checking server found the requested file
            String response =
                    input.readUTF();

            if (!response.equals(
                    "FILE_OK"
            )) {

                return null;
            }

            // receiving file name and size from server
            String receivedFileName =
                    input.readUTF();

            long fileSize =
                    input.readLong();

            File outputFile =
                    new File(
                            directory,
                            createSafeFileName(
                                    receivedFileName
                            )
                    );

            try (
                    FileOutputStream fileOutput =
                            new FileOutputStream(
                                    outputFile
                            )
            ) {
            	
            	// receive the file data
                byte[] buffer =
                        new byte[8192];

                long remaining =
                        fileSize;

                while (remaining > 0) {

                    int bytesToRead =
                            (int) Math.min(
                                    buffer.length,
                                    remaining
                            );

                    int bytesRead =
                            input.read(
                                    buffer,
                                    0,
                                    bytesToRead
                            );
                    
                    // stop if the connection closes before the file is complete
                    if (bytesRead == -1) {
                        throw new IOException(
                                "Download interrupted."
                        );
                    }

                    fileOutput.write(
                            buffer,
                            0,
                            bytesRead
                    );

                    remaining -= bytesRead;
                }
            }

            return outputFile;
        }
    }

    // avoid unsafe file paths when saving files
    private String createSafeFileName(
            String fileName) {

        if (fileName == null
                || fileName.trim().isEmpty()) {

            return "downloaded_file";
        }

        return new File(
                fileName
        ).getName();
    }
}