package chatserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferService extends Thread {

	// separate port use for sending and receiving files
    private static final int FILE_PORT = 5001;

    private static final String SERVER_FILE_DIRECTORY =
            "server_files";

    @Override
    public void run() {

        System.out.println(
                "Starting file transfer service..."
        );

        // create folder to store uploaded files
        File directory =
                new File(
                        SERVER_FILE_DIRECTORY
                );

        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (
        		// listen for file transfer connections
                ServerSocket serverSocket =
                        new ServerSocket(FILE_PORT)
        ) {

            System.out.println(
                    "File transfer service running on port "
                    + FILE_PORT
            );

            while (true) {

                Socket socket =
                        serverSocket.accept();

                System.out.println(
                        "File connection received from: "
                        + socket.getInetAddress()
                );

                // handle each file transfer in its own thread
                FileTransferHandler handler =
                        new FileTransferHandler(socket);

                handler.start();
            }

        } catch (IOException e) {

            System.out.println(
                    "File transfer service error: "
                    + e.getMessage()
            );
        }
    }

    private static class FileTransferHandler
            extends Thread {

        private final Socket socket;

        public FileTransferHandler(
                Socket socket) {

            this.socket = socket;
        }

        @Override
        public void run() {

            try (
                    DataInputStream input =
                            new DataInputStream(
                                    new BufferedInputStream(
                                            socket.getInputStream()
                                    )
                            );

                    DataOutputStream output =
                            new DataOutputStream(
                                    new BufferedOutputStream(
                                            socket.getOutputStream()
                                    )
                            )
            ) {

            	// find out if client wants to upload or download
                String operation =
                        input.readUTF();

                if (operation.equals("UPLOAD")) {

                    receiveFile(
                            input,
                            output
                    );

                } else if (
                        operation.equals("DOWNLOAD")
                ) {

                    sendFile(
                            input,
                            output
                    );

                } else {

                    output.writeUTF(
                            "ERROR|Unknown file operation"
                    );

                    output.flush();
                }

            } catch (EOFException e) {

                System.out.println(
                        "File connection closed."
                );

            } catch (IOException e) {

                System.out.println(
                        "File transfer error: "
                        + e.getMessage()
                );

            } finally {

            	// close the file connection when transfer is finished
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore close error
                }
            }
        }

        private void receiveFile(
                DataInputStream input,
                DataOutputStream output)
                throws IOException {

            String sender =
                    input.readUTF();

            String recipient =
                    input.readUTF();

            String originalFileName =
                    input.readUTF();

            long fileSize =
                    input.readLong();

            System.out.println(
                    "Receiving file: "
                    + originalFileName
                    + " from "
                    + sender
                    + " for "
                    + recipient
            );

            // remove unsafe path info
            String safeFileName =
                    createSafeFileName(
                            originalFileName
                    );

            File directory =
                    new File(
                            SERVER_FILE_DIRECTORY
                    );

            if (!directory.exists()) {
                directory.mkdirs();
            }

            // add timeStamp so files with the same name do not overwrite
            File storedFile =
                    new File(
                            directory,
                            System.currentTimeMillis()
                                    + "_"
                                    + safeFileName
                    );

            try (
                    FileOutputStream fileOutput =
                            new FileOutputStream(
                                    storedFile
                            )
            ) {

                byte[] buffer =
                        new byte[8192];

                long remaining =
                        fileSize;

                // read file in small chunks until the full file is received
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

                    if (bytesRead == -1) {

                        throw new EOFException(
                                "File transfer interrupted."
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

            // let the client know upload was successful
            output.writeUTF(
                    "UPLOAD_OK"
            );

            output.flush();

            System.out.println(
                    "File received successfully: "
                    + storedFile.getName()
            );

            AuditLogger.log(
                    "FILE_UPLOAD",
                    sender,
                    "Sent "
                    + originalFileName
                    + " to "
                    + recipient
                    + " ("
                    + fileSize
                    + " bytes)"
            );

            /// notify recipient if they are online
            ConnectedUser target =
                    SessionRegistry.find(
                            recipient
                    );

            if (target != null) {

                target.send(
                        "FILE_AVAILABLE|"
                        + sender
                        + "|"
                        + storedFile.getName()
                        + "|"
                        + fileSize
                );
            }
        }

        private void sendFile(
                DataInputStream input,
                DataOutputStream output)
                throws IOException {

            String username =
                    input.readUTF();

            String fileName =
                    input.readUTF();

            System.out.println(
                    "Download request from "
                    + username
                    + ": "
                    + fileName
            );

            // find the requested file in the server storage folder
            File serverFile =
                    new File(
                            SERVER_FILE_DIRECTORY,
                            fileName
                    );

            if (!serverFile.exists()
                    || !serverFile.isFile()) {

                output.writeUTF(
                        "ERROR|File not found"
                );

                output.flush();

                AuditLogger.log(
                        "FILE_DOWNLOAD_FAILED",
                        username,
                        "File not found: "
                        + fileName
                );

                return;
            }

            // send file info before sending actual data
            output.writeUTF(
                    "FILE_OK"
            );

            output.writeUTF(
                    serverFile.getName()
            );

            output.writeLong(
                    serverFile.length()
            );

            try (
                    FileInputStream fileInput =
                            new FileInputStream(
                                    serverFile
                            )
            ) {

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
            }

            output.flush();

            System.out.println(
                    "File sent to "
                    + username
                    + ": "
                    + fileName
            );

            AuditLogger.log(
                    "FILE_DOWNLOAD",
                    username,
                    "Downloaded "
                    + fileName
                    + " ("
                    + serverFile.length()
                    + " bytes)"
            );
        }

        // keep file names safe when saving them on the server
        private static String createSafeFileName(
                String fileName) {

            if (fileName == null
                    || fileName.trim().isEmpty()) {

                return "unnamed_file";
            }

            return new File(
                    fileName
            ).getName();
        }
    }
}