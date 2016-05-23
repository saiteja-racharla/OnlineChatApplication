package com.cmpe207.server;
import java.sql.*;
import javax.swing.*;

public class MySqlConnect {
   Connection conn=null;
   public static Connection ConnectDB(){
       try{
           Class.forName("com.mysql.jdbc.Driver");
           Connection conn=DriverManager.getConnection("jdbc:mysql://localhost/java_chat_application_cmpe207","root","root");
           return conn;
       }
       catch(Exception e){ 
		   JOptionPane.showMessageDialog(null,e);
		   return null;
       }   
    }
}