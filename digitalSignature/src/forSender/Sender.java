package forSender;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;

import javax.crypto.Cipher;

public class Sender {
	
	public static void main(String[] args)
	{
		try {
			
			/**
			 * Creating an instance for private key  
			 */
			String storePass = "nesta123";
			
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new FileInputStream("sender_keystore.p12"), storePass.toCharArray());
			PrivateKey privateKey = 
			  (PrivateKey) keyStore.getKey("senderKeyPair",storePass.toCharArray());
			
			/**
			 * Hashing our message so we get our digest  
			 */
			byte[] messageBytes = Files.readAllBytes(Paths.get("message.txt"));
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageHash = md.digest(messageBytes);
			
			/**
			 * encrypting the digest we got with our private key so we create the digital signature
			 */
			
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] digitalSignature = cipher.doFinal(messageHash);
			
			Files.write(Paths.get("digital_signature"), digitalSignature);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
