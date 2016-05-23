package com.cmpe207.server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.swing.*;

import com.cmpe207.client.Client1;
import com.cmpe207.server.MySqlConnect;
public class Server extends JFrame  {
	ArrayList clientOutputStreams;
	ServerSocket serverSock;
	Hashtable<String, String> usersOnline;
	HashMap<String,Socket> hm=new HashMap<String,Socket>();
	public Server() 
    {
        initComponents();
    }
	public void initComponents(){
		jScrollPane1 = new javax.swing.JScrollPane();
        info_textarea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        users_textarea = new javax.swing.JTextArea();
        start_server_button = new javax.swing.JButton();
        stop_server_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        info_textarea.setColumns(20);
        info_textarea.setRows(5);
        info_textarea.setEditable(false);
        users_textarea.setEditable(false);
        jScrollPane1.setViewportView(info_textarea);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        users_textarea.setColumns(20);
        users_textarea.setRows(5);
        users_textarea.setToolTipText("Users Online");
        jScrollPane2.setViewportView(users_textarea);

        start_server_button.setText("Start Server");
        start_server_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_server_buttonActionPerformed(evt);
            }
        });

        stop_server_button.setText("Stop Server");
        stop_server_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop_server_buttonActionPerformed(evt);
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(start_server_button, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stop_server_button, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start_server_button)
                    .addComponent(stop_server_button)))
            .addComponent(jScrollPane2)
        );

        pack();
	}
	private void start_server_buttonActionPerformed(java.awt.event.ActionEvent evt) {                                                    
		Thread starter = new Thread(new ServerStart());
        starter.start();
        
        info_textarea.append("Server started...\n");
    }   
	private void stop_server_buttonActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        clientOutputStreams.clear();
        info_textarea.append("Server Stopped");
        try {
			serverSock.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    } 
	public static void main(String args[]) 
    {
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            @Override
            public void run() {
                new Server().setVisible(true);
            }
        });
    }
	public class ServerStart implements Runnable {
		Connection conn=null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		@Override
		public void run() {
			clientOutputStreams = new ArrayList();
			usersOnline = new Hashtable<String,String>();  
			try {
				serverSock = new ServerSocket(8207);
				while (true) 
                {
					Socket clientSock = serverSock.accept();
					InputStreamReader isReader = new InputStreamReader(clientSock.getInputStream());
	                BufferedReader reader = new BufferedReader(isReader);
	                String data = reader.readLine();
	                String[] usernameAndPassword = data.split(":");
	                String username = usernameAndPassword[0];
	                String password = usernameAndPassword[1];
                	PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                	if(usersOnline.get(username)==null){
                		if(username!=null && password!=null){
    	                	conn=MySqlConnect.ConnectDB();
    	                    String Sql="select * from user_login where user_id=? and password=?";	                   
    	                    pst=conn.prepareStatement(Sql);
    	                    pst.setString(1,username);
    	                    pst.setString(2,password);
    	                    rs=pst.executeQuery();
    	                    if(rs.next()){
    							clientOutputStreams.add(writer);
    							writer.println("Connected");
    							writer.flush();
    							Thread listener = new Thread(new ClientHandler(clientSock, writer));
    							listener.start();
    							info_textarea.append("Accepted New Connection\n");
    	                    }
    	                    else{
    	                    	writer.println("Not Connected");
    	                    }
    	                }
                	}
                	else{
                		writer.println("UserExists");
						writer.flush();
                	}
                }
			} catch (Exception e) {
				
			}
		}
	}
	public class ClientHandler implements Runnable{
		BufferedReader reader;
	    Socket sock;
	    PrintWriter client;
		public ClientHandler(Socket clientSocket, PrintWriter user){
			client = user;
			try 
            {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            }
            catch (Exception ex) 
            {
                info_textarea.append("Unexpected error... \n");
            }
		}
		@Override
		public void run() {
			String message, chat = "Chat" ;
            String[] data;
			try {
				while ((message = reader.readLine()) != null) {
					info_textarea.append("Received: " + message + "\n");
					data = message.split(":");
					if (data[2].equals("Connect")) 
                    {
						hm.put(data[3],sock);
                        userAdd(data[0]);
						tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                    }
					else if (data[2].equals("Chat")) 
                    {
						sendMessage(data[0],data[1],data[3]);
                    } 
					else if(data[2].equals("Disconnected")){
						usersOnline.remove(data[0]);
						sendOnlineUsersToClients(usersOnline);
						showUsersOnlineInServer(usersOnline);
					}
                    else 
                    {
                    	info_textarea.append("No Conditions were met. \n");
                    }
				}
			} catch (Exception e) {
				info_textarea.append("Lost a connection. \n");
                e.printStackTrace();
                clientOutputStreams.remove(client);
			}
		}
	}
	public synchronized void sendMessage(String from,String message,String to) throws IOException{
		Socket sock=hm.get(to);
		PrintWriter writer = new PrintWriter(sock.getOutputStream());
		writer.println(from + ":" +message + ":" + "Received Message");
		writer.flush();
	}
	public void tellEveryone(String message) {
		Iterator it = clientOutputStreams.iterator();
		while(it.hasNext()){
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				info_textarea.append("Sending: " + message + "\n");
                writer.flush();
                info_textarea.setCaretPosition(info_textarea.getDocument().getLength());
			} catch (Exception e) {
				info_textarea.append("Error telling everyone. \n");
			}
		}
	}
	public void sendOnlineUsersToClients(Hashtable<String,String> usersOnline){
		Iterator it = clientOutputStreams.iterator();
		while(it.hasNext()){
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(": :usersOnline:"+usersOnline);
                writer.flush();
                info_textarea.setCaretPosition(info_textarea.getDocument().getLength());
			} catch (Exception e) {
				info_textarea.append("Error telling everyone. \n");
			}
		}
	}
	public void userAdd (String user) {
		usersOnline.put(user, "connected");
		sendOnlineUsersToClients(usersOnline);
		showUsersOnlineInServer(usersOnline);
		String message, add = ": :Connect", done = "Server: :Done";
        tellEveryone(done);
	}
	public void showUsersOnlineInServer(Hashtable<String,String> users){
		Enumeration<String> userList=users.keys();
		users_textarea.setText("");
		while(userList.hasMoreElements()){
			users_textarea.append(userList.nextElement()+"\n");
		}
	}                    
    private javax.swing.JTextArea info_textarea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton start_server_button;
    private javax.swing.JButton stop_server_button;
    private javax.swing.JTextArea users_textarea; 
}
