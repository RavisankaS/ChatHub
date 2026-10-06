package chatclient;

import javax.swing.*;
import java.awt.*;

public class DashboardWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // colors for ChatHub DashBoard Window
    private static final Color BACKGROUND =
            new Color(30, 30, 30);

    private static final Color PANEL_BACKGROUND =
            new Color(37, 37, 38);

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

    private UserListPanel userListPanel;
    private MessagePanel messagePanel;

    public DashboardWindow(
            ServerConnection connection,
            String username) {

        this.connection = connection;
        this.username = username;

        setTitle(
                "ChatHub - "
                + username
        );

        setSize(
                900,
                600
        );
        
        // making the window usable when resizing
        setMinimumSize(
                new Dimension(
                        750,
                        500
                )
        );

        setDefaultCloseOperation(
                JFrame.DO_NOTHING_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setResizable(true);

        createInterface();
        
        // start thread to listen for server messages
        startReceiver();
    }

    private void createInterface() {

        getContentPane().setBackground(
                BACKGROUND
        );

        setLayout(
                new BorderLayout()
        );

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
        
        JPanel titlePanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                15,
                                10
                        )
                );

        titlePanel.setBackground(
                PANEL_BACKGROUND
        );
        
        // ChatHub Title
        JLabel titleLabel =
                new JLabel(
                        "ChatHub"
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        titleLabel.setForeground(
                ACCENT_COLOR
        );

        titlePanel.add(
                titleLabel
        );
        
        // show current logged-in username
        JPanel userPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                5,
                                10
                        )
                );

        userPanel.setBackground(
                PANEL_BACKGROUND
        );

        JLabel userLabel =
                new JLabel(
                        "Signed in as: "
                        + username
                );

        userLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        userLabel.setForeground(
                SECONDARY_TEXT
        );

        userPanel.add(
                userLabel
        );
        
        // session details button & Logout Button on the header
        JPanel headerButtons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                7
                        )
                );

        headerButtons.setBackground(
                PANEL_BACKGROUND
        );

        JButton sessionButton =
                new JButton(
                        "Session Details"
                );

        JButton logoutButton =
                new JButton(
                        "Logout"
                );

        styleButton(
                sessionButton
        );

        styleButton(
                logoutButton
        );

        headerButtons.add(
                sessionButton
        );

        headerButtons.add(
                logoutButton
        );

        headerPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        headerPanel.add(
                userPanel,
                BorderLayout.CENTER
        );

        headerPanel.add(
                headerButtons,
                BorderLayout.EAST
        );

        add(
                headerPanel,
                BorderLayout.NORTH
        );
        
        // creating online users and messaging panels
        userListPanel =
                new UserListPanel();

        messagePanel =
                new MessagePanel(
                        connection,
                        username
                );

        styleUserListPanel();

        styleMessagePanel();
        
        // selecting user changes the current chat recipients
        userListPanel.setUserSelectionListener(
                selectedUser -> {

                    messagePanel.setRecipient(
                            selectedUser
                    );
                }
        );
        
        // split the DashBoard
        JSplitPane splitPane =
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        userListPanel,
                        messagePanel
                );

        splitPane.setDividerLocation(
                210
        );

        splitPane.setBorder(null);

        splitPane.setBackground(
                BACKGROUND
        );

        splitPane.setContinuousLayout(
                true
        );

        add(
                splitPane,
                BorderLayout.CENTER
        );
        
        // showing current session info
        sessionButton.addActionListener(
                e -> requestSessionInfo()
        );
        
        // logout from the chat
        logoutButton.addActionListener(
                e -> logout()
        );
        
        // asking confirmation to close the window
        addWindowListener(
                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent e) {

                        logout();
                    }
                }
        );
    }
    
    // applying colors
    private void styleUserListPanel() {

        userListPanel.setBackground(
                PANEL_BACKGROUND
        );

        userListPanel.setForeground(
                TEXT_COLOR
        );
    }

    private void styleMessagePanel() {

        messagePanel.setBackground(
                BACKGROUND
        );

        messagePanel.setForeground(
                TEXT_COLOR
        );
    }

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
                        12,
                        8,
                        12
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
        
        // changing button color when mouse over it
        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                BUTTON_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                ACCENT_COLOR
                        );
                    }
                }
        );
    }
    
    // continue listening for messages coming from the server
    private void startReceiver() {

        Thread receiverThread =
                new Thread(() -> {

                    try {

                        String serverMessage;

                        while (
                                (serverMessage =
                                        connection.receive())
                                        != null
                        ) {

                            final String message =
                                    serverMessage;

                            SwingUtilities.invokeLater(
                                    () -> processServerMessage(
                                            message
                                    )
                            );
                        }

                    } catch (Exception e) {

                        SwingUtilities.invokeLater(
                                () -> {

                                    if (isDisplayable()) {

                                        JOptionPane.showMessageDialog(
                                                this,
                                                "Connection to server was lost.",
                                                "Connection Error",
                                                JOptionPane.ERROR_MESSAGE
                                        );

                                        dispose();
                                    }
                                }
                        );
                    }
                });

        receiverThread.setDaemon(true);

        receiverThread.start();
    }
    
    // process different type of messages receiving from server
    private void processServerMessage(
            String serverMessage) {

        System.out.println(
                "Received from server: "
                + serverMessage
        );

        if (
                serverMessage.startsWith(
                        "MESSAGE|"
                )
        ) {

            String[] parts =
                    serverMessage.split(
                            "\\|",
                            3
                    );

            if (parts.length == 3) {

                messagePanel.receiveMessage(
                        parts[1],
                        parts[2]
                );
            }
        }

        else if (
                serverMessage.startsWith(
                        "USERS|"
                )
        ) {

            String users =
                    serverMessage.substring(
                            "USERS|".length()
                    );

            userListPanel.updateUsers(
                    users,
                    username
            );
        }
        
        // show confirmation
        else if (
                serverMessage.startsWith(
                        "DELIVERED|"
                )
        ) {

            messagePanel.showStatus(
                    "Message delivered."
            );
        }
        
        // notify when another user sends a file
        else if (
                serverMessage.startsWith(
                        "FILE_AVAILABLE|"
                )
        ) {

            String[] parts =
                    serverMessage.split(
                            "\\|",
                            4
                    );

            if (parts.length == 4) {

                String sender =
                        parts[1];

                String fileName =
                        parts[2];

                long fileSize = 0;

                try {

                    fileSize =
                            Long.parseLong(
                                    parts[3]
                            );

                } catch (
                        NumberFormatException e
                ) {

                    System.out.println(
                            "Invalid file size."
                    );
                }

                messagePanel.receiveFileNotification(
                        sender,
                        fileName,
                        fileSize
                );
            }
        }
        
        // show session info
        else if (
                serverMessage.startsWith(
                        "SESSION|"
                )
        ) {

            displaySessionInfo(
                    serverMessage
            );
        }
        
        // show error from server
        else if (
                serverMessage.startsWith(
                        "ERROR|"
                )
        ) {

            String error =
                    serverMessage.substring(
                            "ERROR|".length()
                    );

            messagePanel.showStatus(
                    error
            );
        }
        
        // close the connection after logout
        else if (
                serverMessage.equals(
                        "LOGOUT_OK"
                )
        ) {

            connection.close();
        }
    }
    
    // request current session info
    private void requestSessionInfo() {

        connection.send(
                "SESSION"
        );
    }

    private void displaySessionInfo(
            String serverMessage) {

        String[] parts =
                serverMessage.split(
                        "\\|",
                        4
                );

        if (parts.length != 4) {

            return;
        }

        String loginTime =
                parts[1];

        String lastActivity =
                parts[2];

        String active =
                parts[3];

        JOptionPane.showMessageDialog(
                this,
                "Username: "
                + username
                + "\n\n"
                + "Login time: "
                + loginTime
                + "\n"
                + "Last activity: "
                + lastActivity
                + "\n"
                + "Session active: "
                + active,
                "Session Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // logout confirmation
    private void logout() {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (
                result ==
                JOptionPane.YES_OPTION
        ) {

            connection.send(
                    "LOGOUT"
            );

            connection.close();

            dispose();

            SwingUtilities.invokeLater(
                    () -> {

                        LoginWindow login =
                                new LoginWindow();

                        login.setVisible(true);
                    }
            );
        }
    }
}