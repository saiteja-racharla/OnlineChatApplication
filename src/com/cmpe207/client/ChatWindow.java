package com.cmpe207.client;

import java.awt.Color;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class ChatWindow extends javax.swing.JFrame implements Runnable,Observer {
	private String toUsername;
	private String fromUsername;
	public ChatWindow(String toUser,String fromUser) {
        initComponents();
        this.toUsername=toUser;
        to_name_label.setText(toUser);
        fromUsername=fromUser;
        from_name_label.setText(fromUser);
    }

    @SuppressWarnings("unchecked")                        
    private void initComponents() {

    	jScrollPane1 = new javax.swing.JScrollPane();
        chat_messages = new javax.swing.JEditorPane();
        chatMsg_text = new javax.swing.JTextField();
        send_button = new javax.swing.JButton();
        to_user_label = new javax.swing.JLabel();
        to_name_label = new javax.swing.JLabel();
        from_user_label = new java.awt.Label();
        from_name_label = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        file_menu = new javax.swing.JMenu();
        close_menuitem = new javax.swing.JMenuItem();
        edit_menu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        chat_messages.setContentType("text/html");
        jScrollPane1.setViewportView(chat_messages);
        chat_messages.setEditable(false);
        send_button.setText("Send");
        send_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_buttonActionPerformed(evt);
            }
        });

        to_user_label.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        to_user_label.setText("To :");

        to_name_label.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        from_user_label.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        from_user_label.setText("From :");

        from_name_label.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N

        file_menu.setText("File");

        close_menuitem.setText("Close");
        file_menu.add(close_menuitem);

        jMenuBar1.add(file_menu);

        edit_menu.setText("Edit");
        jMenuBar1.add(edit_menu);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
            	System.out.println("Window closed");
            	Client1.frameTable.remove(toUsername);
            }
        });
        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(chatMsg_text, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(send_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(to_user_label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(to_name_label))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(from_user_label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(from_name_label)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(from_user_label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(from_name_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(to_user_label)
                    .addComponent(to_name_label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(send_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chatMsg_text))
                .addGap(0, 0, 0))
        );

        pack();
    }                     
    private void send_buttonActionPerformed(java.awt.event.ActionEvent evt) {                                            
    	//writer.println(this.username + ":" + message_textarea.getText() + ":" + "Chat"+":"+friend_text.getText());
    	String message=chatMsg_text.getText();
    	chatMsg_text.setText("");
    	String from=fromUsername;
    	String to=toUsername;
    	String type="Chat";
    	Client1 cl=new Client1();
    	String str="<span style=\"color:green;font-style:Arial;font-size:12px\">"+from+":"+message+"</span>";
    	try {
			chat_messages.getEditorKit().read(new java.io.StringReader(str), chat_messages.getDocument(),chat_messages.getDocument().getLength() );
		} catch (IOException | BadLocationException e) {
			
			e.printStackTrace();
		}
		chat_messages.setCaretPosition(chat_messages.getDocument().getLength());
    	
    	
    	cl.writeMessageToServer(from, message, type, to);
    }  
    
    public void append(String s){
    	try {
        	Document doc=chat_messages.getDocument();
        	doc.insertString(doc.getLength(),s,null);
		} catch (Exception e) {
			
		}
    }
    
    public static void main(String args[]) {
         try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new ChatWindow().setVisible(true);
            }
        });
    }
                 
    private javax.swing.JTextField chatMsg_text;
    private javax.swing.JMenuItem close_menuitem;
    private javax.swing.JMenu edit_menu;
    private javax.swing.JMenu file_menu;
    private javax.swing.JLabel from_name_label;
    private java.awt.Label from_user_label;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JEditorPane chat_messages;
    private javax.swing.JLabel to_name_label;
    private javax.swing.JButton send_button;
    private javax.swing.JLabel to_user_label;        
	@Override
	public void run() {
		System.out.println("Thread Created");
		
	}

	@Override
	public void update(Observable o, Object arg) {
		String message = arg.toString();
		String[] data = message.split(":");
	}
}
