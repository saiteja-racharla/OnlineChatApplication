package com.cmpe207.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Client1 extends JFrame implements ActionListener{
	private static final LayoutManager V = new GridLayout(0, 1);
	Socket sock;
    static BufferedReader reader;
    static PrintWriter writer;
    static String username;
    int port;
    Boolean isConnected = false;
    MessageDispatcher _dispatcher;
    TreeSet<String> usersOnline = new TreeSet<String>();
    private ConcurrentHashMap<String, JButton> dynamicButtons=new ConcurrentHashMap<String, JButton>();
    static Hashtable frameTable=new Hashtable();
    public Client1(String username,String password,int port) 
    {
    	Client1.username=username;
    	_dispatcher=new MessageDispatcher(this);
        initComponents();
        String connectStatus = connect(username,password,port);
        if(connectStatus.equals("Connected"))
        	ListenThread();
    }
    public Client1(String fromUsername){
    	this.username=fromUsername;
    }
    public Client1() {
		
	}
	public static void main(String args[]) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() 
            {
               // new Client().setVisible(true);
            }
        });
    }
    public void initComponents(){
        jTextArea2 = new javax.swing.JTextArea();
        
        new javax.swing.JLabel();
        username_text = new javax.swing.JTextField();
        menubar=new JMenuBar();
        fileMenu=new JMenu("File");
        helpMenu=new JMenu("Help");
        logout=new JMenuItem("logout");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	logout_menuitemActionPerformed(evt);
            }
        });
        fileMenu.add(logout);
        menubar.add(fileMenu);
        menubar.add(helpMenu);
        panel=new JPanel();
        panel.setLayout(V);
        panel.setPreferredSize(new Dimension(200, 320));
        connect_status_message = new JLabel("");
        this.setJMenuBar(menubar);
        chat_textarea = new javax.swing.JTextField();
        chat_textarea.setPreferredSize(new Dimension(2, 20));
        
        message_textarea = new javax.swing.JTextArea();
       
        users_textarea = new javax.swing.JTextArea();
        send_button = new javax.swing.JButton();
        friend_text = new javax.swing.JTextField();

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
            	System.out.println("Window closed");
            	writer.println(username+": :Disconnected");
            	writer.flush();
                System.exit(0);
            }
        });
     
        chat_textarea.setSize(new Dimension(2, 10));
        message_textarea.setColumns(20);
        message_textarea.setRows(5);
        users_textarea.setColumns(20);
        users_textarea.setRows(5);
       

        send_button.setText("Send");
        send_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_buttonActionPerformed(evt);
            }
        });
        this.setTitle("Connected");
        this.add(panel);
        panel.setBackground(Color.white);
        pack();
    }
    private void logout_menuitemActionPerformed(java.awt.event.ActionEvent evt) {
    	frameTable.clear();
    	new Login().setVisible(true);
    	this.dispose();
    }  
    
    private String connect(String username,String password,int port){
    	String returnString = null;
    	if(isConnected==false){
    		username_text.setEditable(false);
    		
    		try {
    			InetAddress ipAddress=Inet4Address.getByName("localhost");
				sock=new Socket(ipAddress, port);
				Client1.username = username;
				InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
				Client1.reader = new BufferedReader(streamreader);
                Client1.writer = new PrintWriter(sock.getOutputStream());
                Client1.writer.println(username+":"+password);
                Client1.writer.flush();
                returnString = reader.readLine();
                if(returnString.equals("Connected")){
                	Client1.writer.println(username + ":has connected.:Connect:"+username);
                    Client1.writer.flush(); 
                }
                
			} catch (Exception e) {
                username_text.setEditable(true);
			}
    	}
    	else if (isConnected == true) 
        {
    		chat_textarea.setText("You are already connected. \n");
        }
    	return returnString;
    }
    private void send_buttonActionPerformed(java.awt.event.ActionEvent evt) {                                            
    	String nothing = "";
    	if ((message_textarea.getText()).equals(nothing)) {
    		message_textarea.setText("");
    		message_textarea.requestFocus();
        }
    	else{
    		try {
    			writeMessageToServer(this.username , message_textarea.getText(), "Chat", friend_text.getText());
            } catch (Exception ex) {
                  
            }
            message_textarea.setText("");
            message_textarea.requestFocus();
    	}
    	message_textarea.setText("");
    	message_textarea.requestFocus();
    }
    public void writeMessageToServer(String fromUserName,String Message, String type,String toUsername){
    	try {
			String encryptedMessage = Encryption.encryptData(Message);
			Client1.writer.println(fromUserName + ":" + encryptedMessage + ":" +type +":"+toUsername);
	    	Client1.writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public void ListenThread() 
    {
         Thread IncomingReader = new Thread(new IncomingReader());
         IncomingReader.start();
    }
    public class IncomingReader implements Runnable{
		@Override
		public void run() {
			String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";
            try {
            	while ((stream = Client1.reader.readLine()) != null) 
                {
            		data = stream.split(":");
            		if (data[2].toString().equals(chat)) 
                    {
                       chat_textarea.setText("You are connected to the server\n");
                       panel.add(connect_status_message);
                    } 
                    else if (data[2].toString().equals(connect))
                    {
                       userAdd(data[0].toString());
                    } 
                    else if(data[2].toString().equals("Received Message")){
                    	_dispatcher.dispatchMessage(stream);
                    }
                    else if(data[2].toString().equals("usersOnline")){
                    	String[] users=data[3].split(",");
                    	int length=users.length;
                    	users_textarea.setText("");
                		if(dynamicButtons!=null && !(dynamicButtons.isEmpty())){
                    		Set<String> s=dynamicButtons.keySet();
                    		Iterator<String> it=s.iterator();
                    		while(it.hasNext()){
                    			String name =(String)it.next();
                        		JButton b = dynamicButtons.remove(name);
                        	    panel.remove(b);
                        	    panel.invalidate();
                    		}
						}
                    	
                    	while(length-1>=0){
                    		System.out.println(users[length-1].indexOf("="));
                    		users_textarea.append(users[length-1].substring(1, users[length-1].indexOf("="))+"\n");
                    		String username=users[length-1].substring(1, users[length-1].indexOf("="));
                    		if(username.equals(Client1.username)){
                    			//do nothing
                    		}
                    		else{
                    			JButton button=new JButton(username);
                    			button.addActionListener(new Client1(Client1.username));
                           	    panel.add(button);
                           	    dynamicButtons.put(username, button);
                           	    panel.revalidate();
                    		}
                       	    length--;
                    	}
                    }
                }
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
    }
    public void createFrame(String fromUser,String message)
	{
		ChatWindow newChatWindow ;
		synchronized(Client1.frameTable) {	
			newChatWindow = (ChatWindow) Client1.frameTable.get(fromUser);
			if(newChatWindow == null) {
				newChatWindow = new ChatWindow(fromUser,Client1.username);
				newChatWindow.setLocation(500, 500);
				Client1.frameTable.put(fromUser,newChatWindow);
				newChatWindow.setVisible(true);
				getDispatcher().addObserver(newChatWindow);
			}
			try {
				String str="<span style=\"color:red;font-style:Arial;font-size:12px\">"+fromUser+":"+message+"</span>";
				newChatWindow.chat_messages.getEditorKit().read(new java.io.StringReader(str), newChatWindow.chat_messages.getDocument(),newChatWindow.chat_messages.getDocument().getLength() );
				newChatWindow.chat_messages.setCaretPosition(newChatWindow.chat_messages.getDocument().getLength());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		ChatWindow h;
		synchronized (Client1.frameTable) {
			h = (ChatWindow) Client1.frameTable.get(this.username);
			h=new ChatWindow(action,this.username);
			Client1.frameTable.put(action, h);
			Thread t=new Thread(h);
			t.start();
			h.setVisible(true);
		}
	}
    public void userAdd(String data) 
    {
         usersOnline.add(data);
    }
    public MessageDispatcher getDispatcher()
	{
		return _dispatcher;
	}

    public void writeUsers() 
    {
         String[] tempList = new String[(usersOnline.size())];
         usersOnline.toArray(tempList);
         users_textarea.setText("");
         for (String token:tempList) 
         {
             users_textarea.append(token + "\n");
         }
    }   
    JPanel panel;
	FlowLayout layout;
    private javax.swing.JTextField chat_textarea;
    private javax.swing.JTextField friend_text;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea message_textarea;
    private javax.swing.JLabel connect_status_message;
    private javax.swing.JButton send_button;
    private javax.swing.JTextField username_text;
    private javax.swing.JTextArea users_textarea;
    private JMenuBar menubar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem logout;
}
class MessageDispatcher extends java.util.Observable
{
	Client1 frame;

	public MessageDispatcher(Client1 frame)
	{
		this.frame=frame;
	}

	void dispatchMessage(String message) throws Exception
	{
		setChanged();
		String data[]=message.split(":");
		String decryptedMessage=Encryption.decryptData(data[1]);
		frame.createFrame(data[0],decryptedMessage);
		notifyObservers(message);
	}
}