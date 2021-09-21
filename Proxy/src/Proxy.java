import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Le proxy crée une socket serveur qui attend les connexions sur le port spécifié.
 * Une fois qu’une connexion arrive et qu’une socket est acceptée, le proxy crée un objet Requesthandler
 * sur un nouveau Thread et lui passe le socket à manipuler.
 * Cela permet au Proxy de continuer à accepter d’autres connexions pendant que d’autres sont traitées.
 *
 * La classe Proxy est également responsable de la gestion dynamique du proxy via la console
 * et est exécuté sur un thread séparé afin de ne pas interrompre l’acceptation des connexions de socket.
 * Cela permet à l’administrateur de bloquer dynamiquement les sites Web en temps réel.
 *
 * Le serveur proxy est également responsable de la mise en cache des copies des sites Web qui sont demandés par
 * les clients et cela comprend le balisage HTML, les images, les fichiers css et js associés à chaque page Web.
 *
 * Lors de la fermeture du serveur proxy, les hashmaps qui contiennent les éléments mis en cache et les sites bloqués sont sérialisés et
 * écrit dans un fichier et chargé à nouveau lorsque le Proxy est redémarré, ce qui signifie qu’il a été mis en cache et bloqué
 * les sites sont entretenus.
 *
 */
public class Proxy implements Runnable{


	// la Principale metheode du programme
	public static void main(String[] args) {
		// Crée une nouvelle instance du Proxy et commence d'entendre le canal pour les connexions
		Proxy myProxy = new Proxy(8085);
		myProxy.listen();	
	}


	private ServerSocket serverSocket;

	
	private volatile boolean running = true;


	/**
	 * Structure de données pour la recherche par ordre constant des éléments de cache.
	 * Clé : URL de la page/image demandée.
	 * Valeur : Fichier stocké associé à cette clé.
	 */
	static HashMap<String, File> cache;

	/**
	 * Structure de données pour la recherche d’ordre constant des sites bloqués.
	 * Clé : URL de la page/image demandée.
	 * Valeur : URL de la page/image demandée.
	 */
	static HashMap<String, String> blockedSites;

	/**
	 * Arraylist des threads en cours d’exécution et de traitement des demandes.
	 * Cette liste est nécessaire pour joindre tous les threads à la fermeture du serveur
	 */
	static ArrayList<Thread> servicingThreads;



	/**
	 * Crée le serveur Proxy
	 * @param port: le numero de port d'où s'execute le proxy  
	 */
	public Proxy(int port) {

		// Charger le Hashmap contenant les sites précédemment mis en cache et les sites bloqués
		cache = new HashMap<>();
		blockedSites = new HashMap<>();

		// Créer un ArrayList pour maintenir les threads de maintenance
		servicingThreads = new ArrayList<>();

		// Démarrez le gestionnaire dynamique sur un thread séparé.
		new Thread(this).start();	// Démarre la méthode run() en bas

		try{
			// Charger les sites mis en cache à partir du fichier
			File cachedSites = new File("cachedSites.txt");
			if(!cachedSites.exists()){
				System.out.println("No cached sites found - creating new file");
				cachedSites.createNewFile();
			} else {
				FileInputStream fileInputStream = new FileInputStream(cachedSites);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				cache = (HashMap<String,File>)objectInputStream.readObject();
				fileInputStream.close();
				objectInputStream.close();
			}

			// Charger les sites bloqués à partir du fichier
			File blockedSitesTxtFile = new File("blockedSites.txt");
			if(!blockedSitesTxtFile.exists()){
				System.out.println("No blocked sites found - creating new file");
				blockedSitesTxtFile.createNewFile();
			} else {
				FileInputStream fileInputStream = new FileInputStream(blockedSitesTxtFile);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				blockedSites = (HashMap<String, String>)objectInputStream.readObject();
				fileInputStream.close();
				objectInputStream.close();
			}
		} catch (IOException e) {
			System.out.println("Error loading previously cached sites file");
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found loading in preivously cached sites file");
			e.printStackTrace();
		}

		try {
			// Créer la socket serveur pour le proxy
			serverSocket = new ServerSocket(port);

			// Définir le délai d’attente
			System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..");
			running = true;
		} 

		// Exceptions de capture associées à la douille d’ouverture		
		catch (SocketException se) {
			System.out.println("Socket Exception when connecting to client");
			se.printStackTrace();
		}
		catch (SocketTimeoutException ste) {
			System.out.println("Timeout occured while connecting to client");
		} 
		catch (IOException io) {
			System.out.println("IO exception when connecting to client");
		}
	}


	/**
	 * Écoute le port et accepte les nouvelles connexions de socket. 
	 * Crée un nouveau thread pour gérer la requête et lui passe la connexion du socket et continue d’écouter.
	 */
	public void listen(){

		while(running){
			try {
				// serverSocket.accpet() Bloque jusqu’à ce qu’une connexion soit établie
				Socket socket = serverSocket.accept();
				
				// Créer un nouveau fil de discussion(Thread) et lui passer RequestHandler
				Thread thread = new Thread(new RequestHandler(socket));
				
				// Saisir une référence à chaque fil (Thread) afin qu’il puisse être joint ultérieurement si nécessaire
				servicingThreads.add(thread);
				
				thread.start();	
			} catch (SocketException e) {
				// L’exception de socket est déclenchée par le système de gestion pour arrêter le proxy 
				System.out.println("Server closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Enregistre les sites bloqués et mis en cache dans un fichier afin qu’ils puissent être rechargés ultérieurement.
	 * Joint également tous les fils (Threads) Requesthandler qui traitent actuellement les demandes.
	 */
	private void closeServer(){
		System.out.println("\nClosing Server..");
		running = false;
		try{
			FileOutputStream fileOutputStream = new FileOutputStream("cachedSites.txt");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

			objectOutputStream.writeObject(cache);
			objectOutputStream.close();
			fileOutputStream.close();
			System.out.println("Cached Sites written");

			FileOutputStream fileOutputStream2 = new FileOutputStream("blockedSites.txt");
			ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(fileOutputStream2);
			objectOutputStream2.writeObject(blockedSites);
			objectOutputStream2.close();
			fileOutputStream2.close();
			System.out.println("Blocked Site list saved");
			try{
				// Fermer tous les Threads de maintenance
				for(Thread thread : servicingThreads){
					if(thread.isAlive()){
						System.out.print("Waiting on "+  thread.getId()+" to close..");
						thread.join();
						System.out.println(" closed");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			} catch (IOException e) {
				System.out.println("Error saving cache/blocked sites");
				e.printStackTrace();
			}

			// Fermer la socket du serveur
			try{
				System.out.println("Terminating Connection");
				serverSocket.close();
			} catch (Exception e) {
				System.out.println("Exception closing proxy's server socket");
				e.printStackTrace();
			}

		}


		/**
		 * Recherche le fichier dans le cache
		 * @param url du fichier demandé 
		 * @return File si le fichier est mis en cache, null sinon
		 */
		public static File getCachedPage(String url){
			return cache.get(url);
		}


		/**
		 * Ajoute une page au cache
		 * @param urlString URL de la page web à cacher 
		 * @param fileToCache Objet fichier pointant vers le fichier mis en cache
		 */
		public static void addCachedPage(String urlString, File fileToCache){
			cache.put(urlString, fileToCache);
		}

		/**
		 * Vérifie si l'URL est bloqué par le Proxy
		 * @param url URL à vérifié
		 * @return true si l'URL est bloqué, false autrement
		 */
		public static boolean isBlocked (String url){
			if(blockedSites.get(url) != null){
				return true;
			} else {
				return false;
			}
		}


		/**
		 * Crée une interface de gestion qui peut mettre à jour dynamiquement les configurations proxy
		 *    bloqué : liste des sites actuellement bloqués
		 *    mis en cache : Liste les sites actuellement mis en cache
		 *    close : Ferme le serveur proxy
		 *    * : Ajoute * à la liste des sites bloqués
		 **/
		@Override
		public void run() {
			Scanner scanner = new Scanner(System.in);

			String command;
			while(running){
				System.out.println("Enter new site to block, or type \"blocked\" to see blocked sites, \"cached\" to see cached sites, or \"close\" to close server.");
				command = scanner.nextLine();
				if(command.toLowerCase().equals("blocked")){
					System.out.println("\nCurrently Blocked Sites");
					for(String key : blockedSites.keySet()){
						System.out.println(key);
					}
					System.out.println();
				} 

				else if(command.toLowerCase().equals("cached")){
					System.out.println("\nCurrently Cached Sites");
					for(String key : cache.keySet()){
						System.out.println(key);
					}
					System.out.println();
				}


				else if(command.equals("close")){
					running = false;
					closeServer();
				}

				else {
					blockedSites.put(command, command);
					System.out.println("\n" + command + " blocked successfully \n");
				}
			}
			scanner.close();
		} 

	}
