import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClient extends JFrame {
    private JTextArea conversationArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton logoutButton;


    private JLabel fileLabel;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String username;
    private String currentRecipient;
    private Map<String, StringBuilder> conversations = new HashMap<>();
    private JList<String> contactList;
    private DefaultListModel<String> contactListModel;
    private List<String> contacts = new ArrayList<>();
    private File selectedFile;

    private JLabel notificationLabel;

    private JLabel statusLabel;

    private AES aes;

    public ChatClient(String serverAddress, int port, String username, Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        try {
            aes = new AES();
            aes.init();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize AES", e);
        }

        this.username = username;
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;

        selectedFile = null;


        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Conversation Area
        conversationArea = new JTextArea();
        conversationArea.setEditable(false);
        conversationArea.setBackground(new Color(0x03F8C5));

        JScrollPane conversationScrollPane = new JScrollPane(conversationArea);

        // Message Field and Send Button
        JPanel messagePanel = new JPanel(new BorderLayout());


        messageField = new JTextField();
        messageField.setBackground(new Color(0xD5CABD));
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(0xD5CABD));
        fileLabel = new JLabel(new ImageIcon(getClass().getResource("icon.jpeg")));
        fileLabel.setCursor(new Cursor(12));

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        messagePanel.add(fileLabel, BorderLayout.WEST);

        sendButton.addActionListener(e -> {
            try {
                sendMessage();
            } catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        });
        fileLabel.addMouseListener(new FileLabelMouseListener());
        //hna

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFont(logoutButton.getFont().deriveFont(Font.BOLD));


        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout(); // Call logout method when button is clicked
            }
        });
        JButton files = new JButton("files");
        files.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accessDownloadedFiles(username);

            }
        });
        //heree
        JButton addContactButton = new JButton("Add Contact");
        addContactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddContactDialog();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(logoutButton);
        buttonPanel.add(addContactButton);
        buttonPanel.add(files);
        getContentPane().add(buttonPanel, BorderLayout.NORTH);


        // Combine Conversation Area and Message Panel
        JPanel conversationPanel = new JPanel(new BorderLayout());
        conversationPanel.add(conversationScrollPane, BorderLayout.CENTER);
        conversationPanel.add(messagePanel, BorderLayout.SOUTH);

        notificationLabel = new JLabel("");
        conversationPanel.add(notificationLabel, BorderLayout.NORTH);

        statusLabel = new JLabel("");
        conversationPanel.add(statusLabel, BorderLayout.NORTH);

        // User List
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addListSelectionListener(e -> startPrivateConversation());
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(150, 0));

        // Contact List
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactList.addListSelectionListener(e -> startContactConversation());
        JScrollPane contactListScrollPane = new JScrollPane(contactList);
        contactListScrollPane.setPreferredSize(new Dimension(150, 0));

        // Combine User List, Contact List, and Conversation Panel
        JPanel userListPanel = new JPanel(new GridLayout(2, 1)); // GridLayout with 2 rows and 1 column
        userListPanel.add(userListScrollPane); // Add user list at the top
        userListPanel.add(contactListScrollPane);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, userListPanel, conversationPanel);
        add(splitPane, BorderLayout.CENTER);


        // Start the message receiver thread
        Thread receiverThread = new Thread(new MessageReceiver());
        receiverThread.start();

        setTitle(username);
        setVisible(true);

        // Request conversation history for all users after outputStream is initialized
        requestConversationHistoryForAllUsers();
    }

    //importaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaannnnnnnnnnnnnnnnnnnnnnnnnnt
    private void showAddContactDialog() {
        // Create a new dialog window
        JDialog dialog = new JDialog(this, true);
        dialog.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Add Contact", SwingConstants.CENTER);
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // Panel for the form fields
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.setPreferredSize(new Dimension(400, 150));

        // Input fields
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();

        // Labels for the input fields
        JLabel nameLabel = new JLabel("Name:");
        JLabel emailLabel = new JLabel("Email:");

        // Add components to the form panel
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // Button to add the contact
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the values from the input fields
                String name = nameField.getText();
                String email = emailField.getText();

                // Close the dialog
                dialog.dispose();

                // Send the information to the server
                sendContactInfoToServer(name, email);
            }
        });


        // Panel for the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);

        // Add components to the dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog size and visibility
        dialog.setSize(400, 200); // Set the size according to your needs
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void sendContactInfoToServer(String name, String email) {
        try {
            // Send a request to the server to insert the contact information
            outputStream.writeObject("ADD_CONTACT");
            outputStream.writeObject(name);
            outputStream.writeObject(email);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class FileLabelMouseListener extends MouseAdapter {
        private FileLabelMouseListener() {
        }

        public void mouseClicked(MouseEvent e) {
            try {
                ChatClient.this.selectFile();
            } catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void selectFile() throws GeneralSecurityException {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == 0) {
            this.selectedFile = fileChooser.getSelectedFile();
            this.sendMessage();
        }

    }

    private static void accessDownloadedFiles(String username) {
        File downloadFolder = new File("files/" + username);

        if (!downloadFolder.exists() || downloadFolder.listFiles() == null || downloadFolder.listFiles().length == 0) {
            JOptionPane.showMessageDialog(null, "No files found in the 'files' folder.");
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Downloaded Files");
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(0, 1)); // Using a grid layout for multiple links

        // Display the list of files in the 'files' folder
        for (File file : downloadFolder.listFiles()) {
            JLabel fileLink = new JLabel(String.format("<html><a href='#'>%s</a></html>", file.getName()));
            fileLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            fileLink.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    int option = JOptionPane.showConfirmDialog(null, "Do you want to download this file?");
                    if (option == JOptionPane.YES_OPTION) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setSelectedFile(new File(file.getName()));

                        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File destination = fileChooser.getSelectedFile();
                            try {
                                Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                JOptionPane.showMessageDialog(null, "File downloaded to: " + destination.getAbsolutePath());
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "Error downloading the file: " + ex.getMessage());
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "File not downloaded.");
                    }
                }
            });

            dialog.add(fileLink); // Add the clickable link to the dialog
        }

        dialog.setVisible(true);
    }

    private void logout() {

        try {
            // Close streams and socket
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Reset UI or switch to login screen
        // For example:
        dispose(); // Close the current chat window
        // You may open a new login window here or perform any other desired action.
    }


    //message history en abscence
    private void requestConversationHistoryForAllUsers() {
        for (int i = 0; i < userListModel.size(); i++) {
            String user = userListModel.getElementAt(i);
            sendConversationHistoryRequest(user);
        }
    }

    private void sendConversationHistoryRequest(String recipient) {
        try {
            outputStream.writeObject("CONVERSATION_HISTORY " + recipient);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //send msg to server
    private void sendMessage(String message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() throws GeneralSecurityException {
        String recipient;
        if (userList.getSelectedValue() != null) {
            recipient = userList.getSelectedValue();
        } else {
            recipient = contactList.getSelectedValue();
        }


        String message = messageField.getText();
        String encryptedMessage = aes.encrypt(message);
        if (!message.isEmpty() && recipient != null && !recipient.isEmpty()) {
            String formattedMessage = "PRIVATE " + recipient + " " + encryptedMessage;
            conversationArea.append("You: " + message + "\n");
            StringBuilder senderConversation = conversations.getOrDefault(recipient, new StringBuilder());
            senderConversation.append("You: ").append(message).append("\n");
            conversations.put(recipient, senderConversation);

            StringBuilder recipientConversation = conversations.getOrDefault(username, new StringBuilder());
            recipientConversation.append(username).append(": ").append(encryptedMessage).append("\n");
            conversations.put(username, recipientConversation);

            sendMessage(formattedMessage);
            messageField.setText("");
        }
        if (selectedFile != null && recipient != null && !recipient.isEmpty()) {

            String fileName = selectedFile.getName();
            String encryptedFileName = aes.encrypt(fileName);
            String fileMessage = "PRIVATE " + recipient + " " + encryptedFileName;
            conversationArea.append("You: " + fileName + "\n");
            StringBuilder senderConversation = conversations.getOrDefault(recipient, new StringBuilder());
            senderConversation.append("You: ").append(selectedFile.getName()).append("\n");
            conversations.put(recipient, senderConversation);

            StringBuilder recipientConversation = conversations.getOrDefault(username, new StringBuilder());
            recipientConversation.append(username).append(": ").append(message).append("\n");
            conversations.put(username, recipientConversation);

//            sendMessage(fileMessage);

            sendMessage(fileMessage);

            File recipientFolder = new File("files", recipient);
            if (!recipientFolder.exists()) {
                recipientFolder.mkdirs(); // mkdirs creates all necessary parent directories as well
            }
            String filePath = recipientFolder.getAbsolutePath() + File.separator + selectedFile.getName();
            File downloadedFile = new File(filePath);
            try {
                Files.copy(selectedFile.toPath(), downloadedFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            selectedFile = null;
        }

    }


    // Modify startPrivateConversation() method
    private void startPrivateConversation() {
        String recipient = userList.getSelectedValue();
        if (recipient != null && !recipient.isEmpty()) {
            currentRecipient = recipient;
            // create a conversation area
            String conversationHistory = conversations.getOrDefault(recipient, new StringBuilder()).toString();
            conversationArea.setText(conversationHistory);

            // Count lines in conversation history
            int notificationCount = conversationHistory.split("\n").length;
            notificationLabel.setText("Notifications: " + notificationCount);
//            statusLabel.setText("Notifications: " + notificationCount);

        }
    }

    // Modify startContactConversation() method
    private void startContactConversation() {
        String contact = contactList.getSelectedValue();
        if (contact != null && !contact.isEmpty()) {
            currentRecipient = contact;
            String conversationHistory = conversations.getOrDefault(contact, new StringBuilder()).toString();
            conversationArea.setText(conversationHistory);

            // Count lines in conversation history
            int notificationCount = conversationHistory.split("\n").length;
            notificationLabel.setText("Notifications: " + notificationCount);
            statusLabel.setText("Notifications: " + notificationCount);

        }
    }


    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String message = (String) inputStream.readObject();
                    if (message.startsWith("USERLIST")) {
                        updateUserList(message.substring(9));
                    } else if (message.startsWith("PRIVATE")) {
                        String[] parts = message.split(" ", 3);
                        String sender = parts[1];
                        String content = parts[2];
                        String decryptedContent = aes.decrypt(content);
                        StringBuilder conversation = conversations.getOrDefault(sender, new StringBuilder());
                        conversation.append(sender).append(": ").append(decryptedContent).append("\n");
                        conversations.put(sender, conversation);
                        if (sender.equals(currentRecipient) || sender.equals(username)) {
                            conversationArea.setText(conversations.getOrDefault(currentRecipient, new StringBuilder()).toString());
                        }
                    } else if (message.startsWith("CONTACTLIST")) {
                        // Update contact list
                        String[] contactList = message.substring(11).split(" ");
                        SwingUtilities.invokeLater(() -> {
                            contactListModel.clear();
                            for (String contact : contactList) {
                                contactListModel.addElement(contact);
                                System.out.println(contactListModel);
                            }
                        });
                    } else if (message.startsWith("FILE_RECEIVED")) {
                        String[] parts = message.split(" ", 3);
                        String sender = parts[1];
                        String fileName = parts[2];
                        ChatClient.this.conversationArea.append(sender + " sent a file: " + fileName + "\n");
                        ChatClient.this.receiveFile(fileName);
                    } else if (message.startsWith("CONVERSATION_HISTORY")) {
                        // Extract sender and content from the message
                        String[] parts = message.split(" ", 3);
                        if (parts.length < 3) {
                            // Handle the case when message is not in expected format
                            return;
                        }

                        String sender = parts[1];
                        String content = parts[2];

                        // Update conversation history
                        StringBuilder conversation = conversations.getOrDefault(sender, new StringBuilder());
//                        conversation.append(sender).append(": ").append(content).append("\n");
                        conversation.append(content).append("\n");
                        conversations.put(sender, conversation);

                        // Print the updated conversation
                        System.out.println(conversation.toString());
                    }

                }
            } catch (EOFException eofException) {
                // Handle EOFException separately
                System.err.println("End of file reached unexpectedly.");
                eofException.printStackTrace();
            } catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
                // Handle other exceptions
                e.printStackTrace();
            }

        }
    }

    private void receiveFile(String fileName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.close();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    //for active users not forcement contact
    private void updateUserList(String userListMessage) {
        String[] users = userListMessage.split(" ");
        userListModel.clear();
        for (String user : users) {
            if (!user.equals(username)) {
                userListModel.addElement(user);
            }
        }
    }
}
