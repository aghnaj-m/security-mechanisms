
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    
        
        Server()
        {
            String info[] = new String[2];
            //Server socket
            ServerSocket server = null;
            // client socket
            Socket client;
            // internal Address
            InetAddress addr;
            
            // lancer le serveur en localhost sur le port 7344
            try{
                addr=InetAddress.getLocalHost(); 
                server = new ServerSocket(7344);
                info[0]=addr.toString();
            }
            // gestion d'erreur en cas d'echec
            catch(IOException ie){
                System.out.println("Cannot Open Socket");
                System.exit(1);
            }
            
            // appel à l'interface graphique
            new ServerWindowThread().start();
            
            // Affichage sur la console
            System.out.println("Welcome !!!\nThis server is running on Local Port Number : " + server.getLocalPort() + "\nWaiting for connections\n");

            info[1]=Integer.toString(server.getLocalPort());
            // envoi d'@IP et port du client vers l'interface graphique
            ServerWindow.getWelcomeMessage(info);
            
            
            // serveur en attente de connexions avec des clients
            while(true) {
            try {
                  client = server.accept();
                 new Thread(new ClientSession(client)).start();
            } catch (IOException ie) {}}
        }
}

class ClientSession implements Runnable {

        private final Socket clientsocket;
        static String info[] = new String[2];
        static String temp;
        // Liste noire
        List<String> list=new ArrayList<>();
        Scanner scanner=new Scanner("block.txt");
        
        
        ClientSession(Socket sock) {
        	// initialisation de socket client
            this.clientsocket = sock;
            temp = clientsocket.getRemoteSocketAddress().toString();
        }

        @Override
        
        public void run() {
            FileInputStream fstream;
            // lecture des @IP interdite stockée sur 'block.txt' et remplissage de la liste noire déclarée précédement
            try 
            {
                fstream = new FileInputStream("block.txt");
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null)   
                {
                    list.add(strLine.trim()); 
                }
            }
            catch(Exception e){}
            //System.out.println(Arrays.toString(list.toArray()));
            try{
            	// declaration des gestionnaires de flux client-serveur
                OutputStream clientOut = clientsocket.getOutputStream();
                PrintWriter pw = new PrintWriter(clientOut, true);
                InputStream clientIn = clientsocket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(clientIn));
                
                //Si l'@IP du client et dispo sur la liste noir une message d'erreur sera affichée au client
                if(list.contains(clientsocket.getRemoteSocketAddress().toString().substring(1, clientsocket.getRemoteSocketAddress().toString().indexOf(":"))))
                {
                    info[0]=clientsocket.getRemoteSocketAddress().toString() + " Blocked";
                    ServerWindow.getConfirmation(info);
                    pw.println("You are Blocked");
                    clientsocket.close();
                }
                // Sinon une connexion est établi
                else
                {
                    info[0]=clientsocket.getRemoteSocketAddress().toString();
                    //pw.println("You are Welcome.");
                    ServerWindow.getConfirmation(info);
                }
                
                //For Message Receiving
                
                Thread tq = new Thread();
                
                while(true)
                {
                	// Attendre un message de la part des clients
                    info[0]=br.readLine();
                    info[1]=clientsocket.getRemoteSocketAddress().toString();
                    
                    // lecture des messages interdits stoqués sur 'stringblock.txt' et remplissage d'une autre liste noir consacré au message cette fois
                    FileInputStream fstreamMes;
                    java.util.List<String> list = new ArrayList<>();
                    try 
                    {
                        fstreamMes = new FileInputStream("stringblock.txt");
                        DataInputStream in = new DataInputStream(fstreamMes);
                        BufferedReader brMes = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        while ((strLine = brMes.readLine()) != null)   
                        {
                            list.add(strLine.trim()); 
                        }
                    }catch(Exception e){}
                    
                    // Verification des messages mot par mot
                    String packet[] = info[0].trim().split("\\s+");
                    String msglist="";
                    ArrayList<String> blockedWords = new ArrayList<>();
                    int l = packet.length;
                    int j;
                    tq.resume();
                    for(j=0;j<l;j++)
                    {
                    	// Si le message est inclu dans la liste noire l'envoi est annulé
                        if(list.contains(packet[j]))
                        {
                            info[0]=packet[j];
                            blockedWords.add(packet[j]);
                        }
                        // Sinon le message est envoyé au serveur
                        else
                        {
                            info[0]=packet[j];
                            ServerWindow.getClientMessage(info);
                        }
                        try 
                        {
                            tq.sleep(100);
                        }
                        catch(Exception e){}
                    }
                    tq.suspend();
                    if(blockedWords.size() > 0)
                    {
                    	String b = "";
                    	for(String s: blockedWords)
                    	{
                    		b+= "'"+s+"', ";
                    	}
                    	// Messages de reponse reçus par le client après la validation
                    	pw.println(b+" blocked !");
                    }else
                    	pw.println("Message accepted !");
                }
            }
            catch(IOException ie)
            {}
        }

    }

class ServerWindowThread extends Thread
{
    public void run()
    {
        ServerWindow sw = new ServerWindow();
    }
}
