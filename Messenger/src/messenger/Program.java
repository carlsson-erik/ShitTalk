package messenger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Erik
 */
public class Program extends JFrame implements Runnable,ActionListener {

    private Thread t;
    private boolean running;
    
    
    //GUI
    
    private JButton clientButton;
    private JButton serverButton;
    
    //Create serverStuff
    
    public Program() {
        
        t = new Thread(this,"Program");
        running = true;
        
        createAndShowGUI();

        t.start();
    }

    public void createAndShowGUI() {
        
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,500);
        this.setLayout(new FlowLayout(1));
        
        clientButton = new JButton("Client");
        clientButton.setPreferredSize(new Dimension(100,100));
        clientButton.setVisible(true);
        clientButton.setName("Client");
        
        serverButton = new JButton("Server");
        serverButton.setPreferredSize(new Dimension(100,100));
        serverButton.setVisible(true);
        
        clientButton.addActionListener(this);
        serverButton.addActionListener(this);
        
        
        this.add(clientButton);
        this.add(serverButton);
        
        this.pack();
        
        
        
        
        
        
    }

    @Override
    public void run() {

        while(running){
            
        }
    }
    
    public void sendMessage(String m){
        
        
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand().toString();

        switch (cmd) {
            case "Client" :
                new clientMessenger();
                
                break;
                
            case "Server" :
               // new serverMessenger();
                break;
        }

    } 
    
    
    
    

}


