package forReceiver;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Arrays;

import javax.crypto.Cipher;

public class Receiver {
	
	public static void main(String[] args)
	{
		try {
			
			/**
			 * Creating  
			 */
			String storePass = "nesta123";
			
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new FileInputStream("receiver_keystore.p12"), storePass.toCharArray());
			// remember always your alias wich is in our case: receiverKeyPair
			Certificate certificate = keyStore.getCertificate("receiverKeyPair");
			PublicKey publicKey = certificate.getPublicKey();
			
			/**
			 * read the received digital signature  
			 */
			byte[] encryptedMessageHash = Files.readAllBytes(Paths.get("digital_signature"));
			/**
			 * We decrypt the digital signature to get the hashed message  
			 */
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] decryptedMessageHash = cipher.doFinal(encryptedMessageHash);
			
			/**
			 * Now we generate new Hash from the received message  
			 */
			byte[] messageBytes = Files.readAllBytes(Paths.get("message.txt"));
			 
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] newMessageHash = md.digest(messageBytes);
			
			/**
			 * We compare between the two hashes  
			 */
			boolean isCorrect = Arrays.equals(decryptedMessageHash, newMessageHash);
			System.out.println("Result: \n"+isCorrect);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
