package com.cmpe207.client;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.*;
	
public class Encryption {
	 
	    private static final byte[] keyValue = 
	        new byte[] { 'R','o','c','k','i','n','g','D','e','s','i','s','L','o','v','e'};

	    
		public static String decryptData(String encryptedData) throws Exception {
	        Key test_obj = generateKey();
	        Cipher c_Obj = Cipher.getInstance("AES");
	        c_Obj.init(Cipher.DECRYPT_MODE, test_obj);
	        byte[] decValue = c_Obj.doFinal(new BASE64Decoder().decodeBuffer(encryptedData));
	        return new String(decValue);
	    }
		

	    public static String encryptData(String decryptedData) throws Exception {
	        Key test = generateKey();
	        Cipher c_Obj = Cipher.getInstance("AES"); 
	        c_Obj.init(Cipher.ENCRYPT_MODE, test); 
	        byte[] encVal = c_Obj.doFinal(decryptedData.getBytes()); 
	        return new BASE64Encoder().encode(encVal);
	    }

	    private static Key generateKey() throws Exception {
	        Key test_obj = new SecretKeySpec(keyValue, "AES");
	        return test_obj;
	    }
}