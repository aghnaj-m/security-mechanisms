import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import javax.imageio.ImageIO;

public class RequestHandler implements Runnable {

	/**
	 * Socket connecté au client passé par le serveur proxy
	 */
	Socket clientSocket;

	/**
	 * Lire les données envoyées par le client au proxy
	 */
	BufferedReader clientToProxyBr;

	/**
	 * Envoyer les données du Proxy vers le client
	 */
	BufferedWriter proxyToClientBw;
	

	/**
	 * Thread utilisé pour transmettre les données lues du client au serveur lors de l’utilisation de HTTPS
	 * Cette référence est nécessaire pour qu’elle puisse être fermée une fois terminée.
	 */
	private Thread httpsClientToServer;


	/**
	 * Crée un objet Reuqesthandler capable de traiter les requêtes GET HTTP(S)
	 * @param clientSocket socket connectée au client
	 */
	public RequestHandler(Socket clientSocket){
		this.clientSocket = clientSocket;
		try{
			this.clientSocket.setSoTimeout(2000);
			clientToProxyBr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			proxyToClientBw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	/**
	 * Lit et examine la demandeString et appelle la méthode appropriée basée sur le type de demande. 
	 */
	@Override
	public void run() {

		// Obtenir la demande du client		
		String requestString;
		try{
			requestString = clientToProxyBr.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading request from client");
			return;
		}

		// Analyse d'URL

		System.out.println("Request Received " + requestString);
		// Obtenir le type de requete
		String request = requestString.substring(0,requestString.indexOf(' '));

		// supprimer le type de demande et l’espace
		String urlString = requestString.substring(requestString.indexOf(' ')+1);

		// Tout enlever après l’espace suivant
		urlString = urlString.substring(0, urlString.indexOf(' '));

		// Prepend http:// si nécessaire pour créer l’URL correcte
		if(!urlString.substring(0,4).equals("http")){
			String temp = "http://";
			urlString = temp + urlString;
		}


		// Vérifier si le site est bloqué
		if(Proxy.isBlocked(urlString)){
			System.out.println("Blocked site requested : " + urlString);
			blockedSiteRequested();
			return;
		}


		// Vérifier le type de demande
		if(request.equals("CONNECT")){
			System.out.println("HTTPS Request for : " + urlString + "\n");
			handleHTTPSRequest(urlString);
		} 

		else{
			// Vérifier si on a une copy cachée
			File file;
			if((file = Proxy.getCachedPage(urlString)) != null){
				System.out.println("Cached Copy found for : " + urlString + "\n");
				
			} else {
				System.out.println("HTTP GET for : " + urlString + "\n");
			}
		}
	} 


	
	/**
	 * Gère les requêtes HTTPS entre le client et le serveur distant
	 * @param urlString fichier souhaité à transmettre via https
	 */
	private void handleHTTPSRequest(String urlString){
		// Extraire l’URL et le port de la télécommande 
		String url = urlString.substring(7);
		String pieces[] = url.split(":");
		url = pieces[0];
		int port  = Integer.valueOf(pieces[1]);

		try{
			// Seule la première ligne de la requête HTTPS a été lue à ce stade (CONNECT *)
			// Lire (et jeter) le reste des données initiales sur le flux
			for(int i=0;i<5;i++){
				clientToProxyBr.readLine();
			}

			// Obtenir l’adresse IP réelle associée à cette URL via DNS
			InetAddress address = InetAddress.getByName(url);
			
			// Ouvrir une socket sur le serveur distant 
			Socket proxyToServerSocket = new Socket(address, port);
			proxyToServerSocket.setSoTimeout(5000);

			// Envoyer la connexion établie au client
			String line = "HTTP/1.0 200 Connection established\r\n" +
					"Proxy-Agent: ProxyServer/1.0\r\n" +
					"\r\n";
			proxyToClientBw.write(line);
			proxyToClientBw.flush();
			
			
			
			// Client et Remote commenceront tous deux à envoyer des données au mandataire à ce stade
			// Le mandataire doit lire de façon asynchrone les données de chaque partie et les envoyer à l’autre partie


			//Créer un proxy et un serveur distant Buffered Writer
			BufferedWriter proxyToServerBW = new BufferedWriter(new OutputStreamWriter(proxyToServerSocket.getOutputStream()));

			// Créer un lecteur tamponné à partir du proxy et du serveur distant			
			BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerSocket.getInputStream()));



			// Créer un nouveau thread pour écouter le client et le transmettre au serveur
			ClientToServerHttpsTransmit clientToServerHttps = 
					new ClientToServerHttpsTransmit(clientSocket.getInputStream(), proxyToServerSocket.getOutputStream());
			
			httpsClientToServer = new Thread(clientToServerHttps);
			httpsClientToServer.start();
			
			
			// Écouter le serveur distant et le relayer au client
			try {
				byte[] buffer = new byte[4096];
				int read;
				do {
					read = proxyToServerSocket.getInputStream().read(buffer);
					if (read > 0) {
						clientSocket.getOutputStream().write(buffer, 0, read);
						if (proxyToServerSocket.getInputStream().available() < 1) {
							clientSocket.getOutputStream().flush();
						}
					}
				} while (read >= 0);
			}
			catch (SocketTimeoutException e) {
				
			}
			catch (IOException e) {
				e.printStackTrace();
			}


			// Fermer les ressources
			if(proxyToServerSocket != null){
				proxyToServerSocket.close();
			}

			if(proxyToServerBR != null){
				proxyToServerBR.close();
			}

			if(proxyToServerBW != null){
				proxyToServerBW.close();
			}

			if(proxyToClientBw != null){
				proxyToClientBw.close();
			}
			
			
		} catch (SocketTimeoutException e) {
			String line = "HTTP/1.0 504 Timeout Occured after 10s\n" +
					"User-Agent: ProxyServer/1.0\n" +
					"\r\n";
			try{
				proxyToClientBw.write(line);
				proxyToClientBw.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} 
		catch (Exception e){
			System.out.println("Error on HTTPS : " + urlString );
			e.printStackTrace();
		}
	}

	


	/**
	 * Écouter les données du client et les transmettre au serveur.
	 * Cela se fait sur un fil (Thread) distinct, comme il faut le faire. 
	 * Lecture asynchrone des données du serveur et transsmetre 
	 * ces données au client. 
	 */
	class ClientToServerHttpsTransmit implements Runnable{
		
		InputStream proxyToClientIS;
		OutputStream proxyToServerOS;
		
		/**
		 * Crée un objet pour écouter le client et transmettre ces données au serveur
		 * @param proxyToClientIS Stream que le Proxy utilise pour recevoir les données du client
		 * @param proxyToServerOS Stream que le proxy utilise pour transmettre des données au serveur distant
		 */
		public ClientToServerHttpsTransmit(InputStream proxyToClientIS, OutputStream proxyToServerOS) {
			this.proxyToClientIS = proxyToClientIS;
			this.proxyToServerOS = proxyToServerOS;
		}

		@Override
		public void run(){
			try {
				// Lire octet par octet à partir du client et envoyer directement au serveur
				byte[] buffer = new byte[4096];
				int read;
				do {
					read = proxyToClientIS.read(buffer);
					if (read > 0) {
						proxyToServerOS.write(buffer, 0, read);
						if (proxyToClientIS.available() < 1) {
							proxyToServerOS.flush();
						}
					}
				} while (read >= 0);
			}
			catch (SocketTimeoutException ste) {
				// TODO: handle exception
			}
			catch (IOException e) {
				System.out.println("Proxy to client HTTPS read timed out");
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Cette méthode est appelée lorsque l’utilisateur demande une page qui est bloquée par le proxy.
	 * Renvoie un message interdit d’accès au client
	 */
	private void blockedSiteRequested(){
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			String line = "HTTP/1.0 403 Access Forbidden \n" +
					"User-Agent: ProxyServer/1.0\n" +
					"\r\n";
			bufferedWriter.write(line);
			bufferedWriter.flush();
		} catch (IOException e) {
			System.out.println("Error writing to client when requested a blocked site");
			e.printStackTrace();
		}
	}
}




