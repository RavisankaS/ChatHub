package chatclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    // adding colors for login window
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

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginWindow() {

        setTitle("ChatHub - Login");

        setSize(
                520,
                520
        );
        
        // keep the login window usable when resize
        setMinimumSize(
                new Dimension(
                        450,
                        450
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setResizable(true);

        createInterface();
    }

    private void createInterface() {
    	
    	// main background of the login window
        JPanel mainPanel =
                new JPanel(
                        new GridBagLayout()
                );

        mainPanel.setBackground(
                BACKGROUND
        );

        mainPanel.setBorder(
                new EmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );
        
        // login form
        JPanel loginPanel =
                new JPanel();

        loginPanel.setLayout(
                new BoxLayout(
                        loginPanel,
                        BoxLayout.Y_AXIS
                )
        );

        loginPanel.setBackground(
                PANEL_BACKGROUND
        );

        loginPanel.setBorder(
                new EmptyBorder(
                        35,
                        40,
                        35,
                        40
                )
        );

        loginPanel.setPreferredSize(
                new Dimension(
                        380,
                        400
                )
        );

        loginPanel.setMinimumSize(
                new Dimension(
                        330,
                        380
                )
        );

        //keeping the login form in the middle
        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        mainPanel.add(
                loginPanel,
                gbc
        );
        
        // application Title
        JLabel titleLabel =
                new JLabel(
                        "ChatHub",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        32
                )
        );

        titleLabel.setForeground(
                ACCENT_COLOR
        );

        titleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        loginPanel.add(
                titleLabel
        );

        loginPanel.add(
                Box.createVerticalStrut(5)
        );
        
        // message shown below the title
        JLabel subtitleLabel =
                new JLabel(
                        "Sign in to continue",
                        SwingConstants.CENTER
                );

        subtitleLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        subtitleLabel.setForeground(
                SECONDARY_TEXT
        );

        subtitleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        loginPanel.add(
                subtitleLabel
        );

        loginPanel.add(
                Box.createVerticalStrut(35)
        );

        JLabel usernameLabel =
                createLabel(
                        "Username"
                );

        loginPanel.add(
                usernameLabel
        );

        loginPanel.add(
                Box.createVerticalStrut(8)
        );

        usernameField =
                new JTextField();

        styleTextField(
                usernameField
        );

        loginPanel.add(
                usernameField
        );

        loginPanel.add(
                Box.createVerticalStrut(18)
        );

        JLabel passwordLabel =
                createLabel(
                        "Password"
                );

        loginPanel.add(
                passwordLabel
        );

        loginPanel.add(
                Box.createVerticalStrut(8)
        );

        passwordField =
                new JPasswordField();

        styleTextField(
                passwordField
        );

        loginPanel.add(
                passwordField
        );

        loginPanel.add(
                Box.createVerticalStrut(28)
        );

        JButton loginButton =
                new JButton(
                        "Login"
                );

        styleButton(
                loginButton
        );
        
        // starting the login process after clicking the button
        loginButton.addActionListener(
                e -> login()
        );

        loginPanel.add(
                loginButton
        );
        
        // pressing enter to login after entering the password
        passwordField.addActionListener(
                e -> login()
        );

        setContentPane(
                mainPanel
        );
        
        // place the cursor in UserName field after opening the window
        SwingUtilities.invokeLater(
                () -> usernameField.requestFocusInWindow()
        );
    }
    
    // creating labels
    private JLabel createLabel(
            String text) {

        JLabel label =
                new JLabel(
                        text
                );

        label.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        label.setForeground(
                TEXT_COLOR
        );

        label.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        return label;
    }
    
    // applying common style to userName and password
    private void styleTextField(
            JTextField field) {

        field.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        field.setMaximumSize(
                new Dimension(
                        300,
                        42
                )
        );

        field.setPreferredSize(
                new Dimension(
                        300,
                        42
                )
        );

        field.setMinimumSize(
                new Dimension(
                        300,
                        42
                )
        );

        field.setBackground(
                INPUT_BACKGROUND
        );

        field.setForeground(
                TEXT_COLOR
        );

        field.setCaretColor(
                TEXT_COLOR
        );

        field.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_COLOR
                        ),
                        new EmptyBorder(
                                6,
                                12,
                                6,
                                12
                        )
                )
        );
    }

    // adding same style to login button
    private void styleButton(
            JButton button) {

        button.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        button.setMaximumSize(
                new Dimension(
                        300,
                        44
                )
        );

        button.setPreferredSize(
                new Dimension(
                        300,
                        44
                )
        );

        button.setMinimumSize(
                new Dimension(
                        300,
                        44
                )
        );

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
                        15
                )
        );

        button.setFocusPainted(
                false
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        15,
                        10,
                        15
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
        
        // changing button color when mouse moves over
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

    // checking login info and connection to the server
    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );
        
        // not continue if either field is empty
        if (username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your username and password.",
                    "Login",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {
        	
        	// creating connecting to ChatHub server
            ServerConnection connection =
                    new ServerConnection(
                            "localhost",
                            5000
                    );

            connection.connect();
            
            // sending userName & password to server
            connection.send(
                    "LOGIN|"
                    + username
                    + "|"
                    + password
            );

            String response =
                    connection.receive();

            if (response == null) {

                connection.close();

                JOptionPane.showMessageDialog(
                        this,
                        "The server did not respond.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // Open the dashBoard when login is successful
            if (response.startsWith(
                    "LOGIN_OK|"
            )) {

                DashboardWindow dashboard =
                        new DashboardWindow(
                                connection,
                                username
                        );

                dashboard.setVisible(
                        true
                );

                dispose();

            } else {

                String message =
                        "Login failed.";

                if (response.contains("|")) {

                    message =
                            response.substring(
                                    response.indexOf("|") + 1
                            );
                }

                connection.close();

                JOptionPane.showMessageDialog(
                        this,
                        message,
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        // getting errors from server
        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Couldn't connect to ChatHub server.\n\n"
                    + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(
            String[] args) {
    	// starting the swing application on the event dispatch thread
        SwingUtilities.invokeLater(
                () -> {

                    LoginWindow window =
                            new LoginWindow();

                    window.setVisible(true);
                }
        );
    }
}