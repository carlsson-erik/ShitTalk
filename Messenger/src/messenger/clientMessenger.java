package messenger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author carls
 */
public class clientMessenger extends JFrame implements ActionListener, Runnable {

    //GUI
    private JButton connectButton;
    private JButton sendButton, leaveButton;

    private JTextArea textArea;
    private JTextField text, ipText, portText;
    private JScrollPane scrollPane;
    
    private JLabel yourIp;

    //Create serverStuff
    ServerSocket serverSocket;
    Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;

    String serverIP;
    int port;

    private Thread t;
    private int type;

    public static void main(String[] args) {
        try {
            // Set System L&F
            //UIManager.setLookAndFeel(
             //       UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new clientMessenger();
    }

    public clientMessenger() {
        t = new Thread(this, "Messenger");

        serverIP = "192.168.1.8";
        port = 33678;
        type = 0;
        createAndShowGUI();
        t.start();

    }

    public void createAndShowGUI() {

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setResizable(false);
        this.setLayout(new FlowLayout(1));

        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(100, 100));
        sendButton.setVisible(true);

        connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(100, 100));
        connectButton.setVisible(true);
        
        leaveButton = new JButton("Leave");
        leaveButton.setPreferredSize(new Dimension(100, 100));
        leaveButton.setVisible(true);
        leaveButton.setEnabled(false);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setVisible(true);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(200, 400));
        scrollPane.setVisible(true);

        text = new JTextField("Text");
        text.setName("Text");
        text.setPreferredSize(new Dimension(200, 50));
        text.setVisible(true);
        
        yourIp = new JLabel();
        try {
            yourIp.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(clientMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ipText = new JTextField("192.168.1.1");
        try {
            ipText.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(clientMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
        ipText.setPreferredSize(new Dimension(200, 50));
        ipText.setVisible(true);

        portText = new JTextField("" + port);
        portText.setPreferredSize(new Dimension(200, 50));
        portText.setVisible(true);

        sendButton.addActionListener(this);
        connectButton.addActionListener(this);
        text.addActionListener(this);
        leaveButton.addActionListener(this);

        this.add(yourIp);
        this.add(ipText);
        this.add(portText);
        this.add(connectButton, BorderLayout.BEFORE_FIRST_LINE);
        this.add(leaveButton);
        this.add(sendButton, BorderLayout.WEST);
        this.add(scrollPane, BorderLayout.EAST);
        this.add(text, BorderLayout.SOUTH);
        
        this.validate();
        this.repaint();
        
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(port, 100);
        } catch (IOException ex) {
            Logger.getLogger(clientMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {

            if (socket == null) {
                try {
                    waitForConnect();
                } catch (IOException e) {

                }
            } else {
                try {
                    setupStreams();
                    whileConnected();
                    closeStreams();
                } catch (IOException ex) {

                    Logger.getLogger(clientMessenger.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
    }

    public void connectToServer() throws IOException {

        showMessage("Attempting to connect...");
        
        socket = new Socket(InetAddress.getByName(ipText.getText()), Integer.parseInt(portText.getText()));

        showMessage("Now connected to: " + socket.getInetAddress().getHostName());
        
    }

    public void waitForConnect() throws IOException {
        showMessage("Waiting for sombody to connect");
        serverSocket.setSoTimeout(1000);
        socket = serverSocket.accept();

        

    }

    public void setupStreams() throws IOException {
        
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());

        
    }

    public void whileConnected() throws IOException {
        String message = "Connected";
        sendMessage(message);
        leaveButton.setEnabled(true);
        sendButton.setEnabled(true);
        do {
            try {
                message = (String) input.readObject();
                showMessage(socket.getInetAddress().getHostAddress() + ": " + message);

            } catch (ClassNotFoundException n) {
                showMessage("User sent some shit");
            }

        } while (true);

    }

    public void sendMessage(String message) {
        
        try {
            output.writeObject(message);
            output.flush();
            showMessage(message);
        } catch (IOException e) {
            showMessage("Could not send that message");

        }

    }

    public void showMessage(String message) {
        textArea.append("\n" + message);

    }

    public void closeStreams() throws IOException {
        output.close();
        input.close();
        socket.close();
        socket = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand().toString();

        switch (cmd) {
            case "Send":
                sendMessage(text.getText());
                break;

            case "Leave":
        {
            try {
                closeStreams();
            } catch (IOException ex) {
                Logger.getLogger(clientMessenger.class.getName()).log(Level.SEVERE, null, ex);
            }
            leaveButton.setEnabled(false);
            sendButton.setEnabled(false);
        }
                break;
                
            case "Connect": {
                try {
                    connectToServer();
                } catch (IOException ex) {
                    Logger.getLogger(clientMessenger.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            break;
        }

    }
}
