package chatclient;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MessagePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // adding colors to chat panel
    private static final Color BACKGROUND =
            new Color(30, 30, 30);

    private static final Color PANEL_BACKGROUND =
            new Color(37, 37, 38);

    private static final Color INPUT_BACKGROUND =
            new Color(48, 48, 48);

    private static final Color BORDER_COLOR =
            new Color(75, 75, 75);

    private static final Color TEXT_COLOR =
            new Color(245, 245, 245);

    private static final Color SECONDARY_TEXT =
            new Color(180, 180, 180);

    private static final Color ACCENT_COLOR =
            new Color(33, 150, 243);

    private static final Color BUTTON_HOVER =
            new Color(25, 118, 210);

    private final ServerConnection connection;
    private final String username;

    private JLabel conversationLabel;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton attachButton;
    private JLabel statusLabel;

    private String recipient;

    public MessagePanel(
            ServerConnection connection,
            String username) {

        this.connection = connection;
        this.username = username;

        createInterface();
    }

    private void createInterface() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                BACKGROUND
        );

        createHeader();

        createMessageArea();

        createBottomPanel();
    }

    // creating header
    private void createHeader() {

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setBackground(
                PANEL_BACKGROUND
        );

        headerPanel.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        BORDER_COLOR
                )
        );

        conversationLabel =
                new JLabel(
                        "Select a user to start chatting"
                );

        conversationLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        conversationLabel.setForeground(
                TEXT_COLOR
        );

        headerPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                BORDER_COLOR
                        ),
                        BorderFactory.createEmptyBorder(
                                12,
                                15,
                                12,
                                15
                        )
                )
        );

        headerPanel.add(
                conversationLabel,
                BorderLayout.WEST
        );

        add(
                headerPanel,
                BorderLayout.NORTH
        );
    }

    // setting up the area for displaying messages
    private void createMessageArea() {

        messageArea =
                new JTextArea();

        messageArea.setEditable(
                false
        );

        messageArea.setLineWrap(
                true
        );

        messageArea.setWrapStyleWord(
                true
        );

        messageArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        messageArea.setBackground(
                BACKGROUND
        );

        messageArea.setForeground(
                TEXT_COLOR
        );

        messageArea.setCaretColor(
                TEXT_COLOR
        );

        messageArea.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        12,
                        10,
                        12
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        messageArea
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.setBackground(
                BACKGROUND
        );

        scrollPane.getViewport().setBackground(
                BACKGROUND
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    // creating message input & action buttons
    private void createBottomPanel() {

        JPanel bottomPanel =
                new JPanel(
                        new BorderLayout(
                                8,
                                8
                        )
                );

        bottomPanel.setBackground(
                PANEL_BACKGROUND
        );

        bottomPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                BORDER_COLOR
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                )
        );

        inputField =
                new JTextField();

        inputField.setBackground(
                INPUT_BACKGROUND
        );

        inputField.setForeground(
                TEXT_COLOR
        );

        inputField.setCaretColor(
                TEXT_COLOR
        );

        inputField.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        inputField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_COLOR
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                8,
                                7,
                                8
                        )
                )
        );

        sendButton =
                new JButton(
                        "Send"
                );

        attachButton =
                new JButton(
                        "Attach File"
                );

        styleButton(
                sendButton
        );

        styleButton(
                attachButton
        );

        JPanel inputPanel =
                new JPanel(
                        new BorderLayout(
                                8,
                                8
                        )
                );

        inputPanel.setBackground(
                PANEL_BACKGROUND
        );

        inputPanel.add(
                inputField,
                BorderLayout.CENTER
        );

        inputPanel.add(
                sendButton,
                BorderLayout.EAST
        );

        JPanel actionPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        actionPanel.setBackground(
                PANEL_BACKGROUND
        );

        actionPanel.add(
                attachButton
        );

        statusLabel =
                new JLabel(
                        "Select a user to start chatting."
                );

        statusLabel.setForeground(
                SECONDARY_TEXT
        );

        statusLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        JPanel statusPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        statusPanel.setBackground(
                PANEL_BACKGROUND
        );

        statusPanel.add(
                actionPanel,
                BorderLayout.WEST
        );

        statusPanel.add(
                statusLabel,
                BorderLayout.CENTER
        );

        bottomPanel.add(
                inputPanel,
                BorderLayout.NORTH
        );

        bottomPanel.add(
                statusPanel,
                BorderLayout.SOUTH
        );

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // sending message after clicking on the button
        sendButton.addActionListener(
                e -> sendMessage()
        );

        // pressing enter sending the message as well 
        inputField.addActionListener(
                e -> sendMessage()
        );

        // opening file chooser
        attachButton.addActionListener(
                e -> chooseAndSendFile()
        );
    }

    // applying same style to buttons
    private void styleButton(
            JButton button) {

        button.setBackground(
                ACCENT_COLOR
        );

        button.setForeground(
                Color.WHITE
        );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setFocusPainted(
                false
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        14,
                        8,
                        14
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        // changing the color when the mouse is over it
        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {

                        if (button.isEnabled()) {

                            button.setBackground(
                                    BUTTON_HOVER
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

                        if (button.isEnabled()) {

                            button.setBackground(
                                    ACCENT_COLOR
                            );
                        }
                    }
                }
        );
    }

    // updates the panel after selecting user
    public void setRecipient(
            String recipient) {

        this.recipient =
                recipient;

        conversationLabel.setText(
                "Conversation with "
                + recipient
        );

        conversationLabel.setForeground(
                TEXT_COLOR
        );

        messageArea.setText(
                ""
        );

        statusLabel.setText(
                "Ready to send a message."
        );

        inputField.requestFocusInWindow();
    }

    // sending typed message to selected user
    private void sendMessage() {

       // select user first
        if (recipient == null
                || recipient.trim().isEmpty()) {

            showStatus(
                    "Select a user first."
            );

            return;
        }

        // getting message from input box
        String message =
                inputField.getText().trim();

        if (message.isEmpty()) {

            showStatus(
                    "Enter a message first."
            );

            inputField.requestFocusInWindow();

            return;
        }

        // checking the server connection
        if (!connection.isConnected()) {

            showStatus(
                    "Not connected to server."
            );

            return;
        }

        try {

            // sending the message using server command format
            connection.send(
                    "SEND|"
                    + recipient
                    + "|"
                    + message
            );

            // show the message in our chat window
            messageArea.append(
                    "You: "
                    + message
                    + "\n"
            );

            messageArea.setCaretPosition(
                    messageArea.getDocument()
                            .getLength()
            );

            inputField.setText(
                    ""
            );

            inputField.requestFocusInWindow();

            showStatus(
                    "Sending..."
            );

        } catch (Exception e) {

            showStatus(
                    "Failed to send message."
            );

            System.out.println(
                    "Send message error: "
                    + e.getMessage()
            );
        }
    }

    // user choose file and send it
    private void chooseAndSendFile() {

        if (recipient == null) {

            showStatus(
                    "Select a user first."
            );

            return;
        }

        JFileChooser fileChooser =
                new JFileChooser();

        int result =
                fileChooser.showOpenDialog(
                        this
                );

        if (
                result != JFileChooser.APPROVE_OPTION
        ) {

            return;
        }

        File selectedFile =
                fileChooser.getSelectedFile();

        attachButton.setEnabled(
                false
        );

        showStatus(
                "Sending file..."
        );
        
        // file transfer running in the background
        Thread fileThread =
                new Thread(() -> {

                    try {

                        FileTransferClient transferClient =
                                new FileTransferClient(
                                        "localhost"
                                );

                        boolean success =
                                transferClient.sendFile(
                                        selectedFile,
                                        username,
                                        recipient
                                );

                        SwingUtilities.invokeLater(
                                () -> {

                                    attachButton.setEnabled(
                                            true
                                    );

                                    if (success) {

                                        messageArea.append(
                                                "You sent: "
                                                + selectedFile.getName()
                                                + "\n"
                                        );

                                        showStatus(
                                                "File sent successfully."
                                        );

                                    } else {

                                        showStatus(
                                                "File could not be sent."
                                        );
                                    }
                                }
                        );

                    } catch (Exception e) {

                        SwingUtilities.invokeLater(
                                () -> {

                                    attachButton.setEnabled(
                                            true
                                    );

                                    showStatus(
                                            "File transfer failed."
                                    );

                                    System.out.println(
                                            "File transfer error: "
                                            + e.getMessage()
                                    );
                                }
                        );
                    }
                });

        fileThread.start();
    }

    // displaying message received from another user
    public void receiveMessage(
            String sender,
            String message) {

        messageArea.append(
                sender
                + ": "
                + message
                + "\n"
        );

        messageArea.setCaretPosition(
                messageArea.getDocument()
                        .getLength()
        );
    }

    // show notification when another user sends a file
    public void receiveFileNotification(
            String sender,
            String fileName,
            long fileSize) {

        messageArea.append(
                sender
                + " sent a file: "
                + fileName
                + " ("
                + formatFileSize(fileSize)
                + ")"
                + "\n"
        );

        JButton downloadButton =
                new JButton(
                        "Download File"
                );

        styleButton(
                downloadButton
        );

        downloadButton.addActionListener(
                e -> downloadFile(
                        sender,
                        fileName,
                        downloadButton
                )
        );

        JPanel filePanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        filePanel.setBackground(
                PANEL_BACKGROUND
        );

        JLabel fileLabel =
                new JLabel(
                        fileName
                );

        fileLabel.setForeground(
                TEXT_COLOR
        );

        filePanel.add(
                fileLabel
        );

        filePanel.add(
                downloadButton
        );

        messageArea.setCaretPosition(
                messageArea.getDocument()
                        .getLength()
        );

        JOptionPane.showMessageDialog(
                this,
                filePanel,
                "File Available",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // download the selected file
    private void downloadFile(
            String sender,
            String fileName,
            JButton downloadButton) {

        downloadButton.setEnabled(
                false
        );

        showStatus(
                "Downloading file..."
        );

        Thread downloadThread =
                new Thread(() -> {

                    try {

                        FileTransferClient transferClient =
                                new FileTransferClient(
                                        "localhost"
                                );

                        File downloadedFile =
                                transferClient.downloadFile(
                                        username,
                                        fileName
                                );

                        SwingUtilities.invokeLater(
                                () -> {

                                    downloadButton.setEnabled(
                                            true
                                    );

                                    if (
                                            downloadedFile
                                            != null
                                    ) {

                                        showStatus(
                                                "File downloaded successfully."
                                        );

                                        int result =
                                                JOptionPane.showConfirmDialog(
                                                        this,
                                                        "File downloaded to:\n"
                                                        + downloadedFile
                                                                .getAbsolutePath()
                                                        + "\n\n"
                                                        + "Would you like to open it?",
                                                        "Download Complete",
                                                        JOptionPane.YES_NO_OPTION
                                                );

                                        if (
                                                result ==
                                                JOptionPane.YES_OPTION
                                        ) {

                                            openFile(
                                                    downloadedFile
                                            );
                                        }

                                    } else {

                                        showStatus(
                                                "Unable to download file."
                                        );

                                        JOptionPane.showMessageDialog(
                                                this,
                                                "The file could not be downloaded.",
                                                "Download Error",
                                                JOptionPane.ERROR_MESSAGE
                                        );
                                    }
                                }
                        );

                    } catch (Exception e) {

                        SwingUtilities.invokeLater(
                                () -> {

                                    downloadButton.setEnabled(
                                            true
                                    );

                                    showStatus(
                                            "Download failed."
                                    );

                                    JOptionPane.showMessageDialog(
                                            this,
                                            "Download failed:\n"
                                            + e.getMessage(),
                                            "Download Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );

                                    System.out.println(
                                            "Download error: "
                                            + e.getMessage()
                                    );
                                }
                        );
                    }
                });

        downloadThread.start();
    }

    // opening the downloaded file using the default application
    private void openFile(
            File file) {

        try {

            if (
                    !Desktop.isDesktopSupported()
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Your system does not support opening files automatically.",
                        "Open File",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            Desktop desktop =
                    Desktop.getDesktop();

            if (
                    !desktop.isSupported(
                            Desktop.Action.OPEN
                    )
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "File opening is not supported on this system.",
                        "Open File",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            desktop.open(
                    file
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open the file:\n"
                    + e.getMessage(),
                    "Open File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // converting file size to readable format
    private String formatFileSize(
            long bytes) {

        if (bytes < 1024) {

            return bytes + " B";
        }

        if (bytes < 1024 * 1024) {

            return String.format(
                    "%.1f KB",
                    bytes / 1024.0
            );
        }

        return String.format(
                "%.1f MB",
                bytes / (1024.0 * 1024.0)
        );
    }

    public void showStatus(
            String status) {

        statusLabel.setText(
                status
        );
    }
}