package chatclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class UserListPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // adding colors for the user panel
    private static final Color PANEL_BACKGROUND =
            new Color(37, 37, 38);

    private static final Color BORDER_COLOR =
            new Color(75, 75, 75);

    private static final Color TEXT_COLOR =
            new Color(245, 245, 245);

    private static final Color ACCENT_COLOR =
            new Color(33, 150, 243);

    private DefaultListModel<String> userListModel;

    private JList<String> userList;

    private Consumer<String> userSelectionListener;

    public UserListPanel() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                PANEL_BACKGROUND
        );

        setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        0,
                        1,
                        BORDER_COLOR
                )
        );

        createInterface();
    }

    // creating online users section
    private void createInterface() {

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setBackground(
                PANEL_BACKGROUND
        );

        headerPanel.setBorder(
                new EmptyBorder(
                        15,
                        15,
                        12,
                        15
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "Online Users"
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        titleLabel.setForeground(
                TEXT_COLOR
        );

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        add(
                headerPanel,
                BorderLayout.NORTH
        );

        // listing userNames shown in the list
        userListModel =
                new DefaultListModel<>();

        userList =
                new JList<>(
                        userListModel
                );

        userList.setBackground(
                PANEL_BACKGROUND
        );

        userList.setForeground(
                TEXT_COLOR
        );

        userList.setSelectionBackground(
                ACCENT_COLOR
        );

        userList.setSelectionForeground(
                Color.WHITE
        );

        userList.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        userList.setFixedCellHeight(
                35
        );

        userList.setBorder(
                new EmptyBorder(
                        5,
                        10,
                        5,
                        10
                )
        );

        userList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        userList
                );

        scrollPane.setBackground(
                PANEL_BACKGROUND
        );

        scrollPane.getViewport().setBackground(
                PANEL_BACKGROUND
        );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // notify dashBoard when a user is selected
        userList.addListSelectionListener(
                e -> {

                    if (!e.getValueIsAdjusting()) {

                        String selectedUser =
                                userList.getSelectedValue();

                        if (
                                selectedUser != null
                                && userSelectionListener != null
                        ) {

                            userSelectionListener.accept(
                                    selectedUser
                            );
                        }
                    }
                }
        );
    }

    // updates the list when server sends new user list
    public void updateUsers(
            String users,
            String currentUsername) {

        SwingUtilities.invokeLater(
                () -> {

                    userListModel.clear();

                    if (
                            users == null
                            || users.trim().isEmpty()
                            || users.equals(
                                    "No users online"
                            )
                    ) {

                        return;
                    }

                    String[] userArray =
                            users.split(
                                    ","
                            );

                    for (
                            String user :
                            userArray
                    ) {

                        String cleanUsername =
                                user.trim();

                        if (
                                !cleanUsername.isEmpty()
                                && !cleanUsername.equals(
                                        currentUsername
                                )
                        ) {

                            userListModel.addElement(
                                    cleanUsername
                            );
                        }
                    }
                }
        );
    }

    // setting action that runs when a user is selected
    public void setUserSelectionListener(
            Consumer<String> listener) {

        this.userSelectionListener =
                listener;
    }
}