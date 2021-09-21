import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;

import javax.swing.JSeparator;
import javax.swing.JScrollPane;

public class Client {
	// définition d'un point de terminaison interne pour l'envoi ou la réception de données
	Socket client;
	// définition des gestionnaires de flux
    InputStream clientIn;
    OutputStream clientOut;
    PrintWriter pw;
    BufferedReader br, stdIn;
    String ip="";
	// un nouveau thread independant du serveur
    Thread tc = new Thread();
	// definition d'un pattern pour la verification du validation des @IPs
    private static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
    Matcher matcher;
	// Déclaration des Composants d'interface graphique
	private JFrame frame;
	private JTextField IpInput;
	private JTextField portInput;
	private JLabel lblLiveChatBg;
	private JTextField msgInput;
	private JLabel lblOnMode;
	private static int lastPosition = 0;

	/**
	 * Create the application.
	 */
	public Client() {
        tc.start();
		initialize();
	}

	/**
	 * Initialisation des composants de l'interface 
	 */
	private void initialize() {
		//initialisation du fenetre
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		//positionnement
		frame.setBounds(100, 100, 350, 510);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel startBtn = new JLabel("");
		startBtn.setBounds(300, 18, 40, 26);
		startBtn.setIcon(new ImageIcon(Client.class.getResource("/startBtn.png")));
		// Code executé lors de clique sur le bouton de connextion
		startBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//For connecting to the server
		        int flag=0;
		        String args[] = new String[2];
		        ip=IpInput.getText();
		        matcher = pattern.matcher(ip);
		        // Si l'@IP entrée est valide
		        if(matcher.matches() && !ip.startsWith("0"))
		        {
					// se connecter au serveur et initialisation des gestionnaires de flux de données client-serveur
		            try
		            {
		                client =new Socket(ip,Integer.parseInt(portInput.getText()));
		                clientIn =client.getInputStream();
		                clientOut =client.getOutputStream();
		                pw = new PrintWriter(clientOut, true);
		                br = new BufferedReader(new InputStreamReader(clientIn));
		                stdIn = new BufferedReader(new InputStreamReader(System.in));

		            }
					//Affichage d'un message au client en cas d'echec de connexion au serveur
		            catch(ConnectException ce)
		            {
		                JOptionPane.showMessageDialog(frame, "Server Not Found");
		                flag=1;
		                System.out.println("Cannot connect to the server.");

		            }
		            catch (IOException ie) 
		            {}
		            FileInputStream fstream;
		            java.util.List<String> list=new ArrayList<>();
		            // Lire les mots interdits stoqués sur le ficher block.txt et les ajoutés dans la list déclarée ci-dessus
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
		            catch(Exception e1){}
					// si l'@IP du client est présente dans la liste noire, un message d'erreur s'affiche , sinon une connexion vers le serveur est bien établi. 
		            if(list.contains(ip))
		            {
		                    try
		                    {
		                        JOptionPane.showMessageDialog(frame, "You're not allowed to access this server", "Banned", JOptionPane.ERROR_MESSAGE);
		                    	client.close();
		                        
		                    }
		                    catch(IOException e1){}
		            }
		            else
		            {
		                if(flag!=1)
		                {
		                    JOptionPane.showMessageDialog(frame, "Successfully Connected to the Server");
		    				lblOnMode.setVisible(true);
		                    System.out.println("Successfully connected to the server.");
		                }
		            }
		        }
		        else
		        {
		            JOptionPane.showMessageDialog(frame, "Please enter valid IP Address");
		        }

			}
		});

		frame.getContentPane().add(startBtn);
		 lblOnMode = new JLabel("");
		lblOnMode.setBounds(0, 70, 350, 89);
		lblOnMode.setIcon(new ImageIcon(Client.class.getResource("/onMode.png")));
		frame.getContentPane().add(lblOnMode);
		lblOnMode.setVisible(false);
		// Champ d'IP
		JLabel lblNewLabel = new JLabel("SERVER IP");
		lblNewLabel.setBounds(6, 23, 69, 16);
		frame.getContentPane().add(lblNewLabel);
		
		IpInput = new JTextField();
		IpInput.setBounds(68, 18, 130, 26);
		IpInput.setDocument(new JTextFieldLimit(15));
		frame.getContentPane().add(IpInput);
		IpInput.setColumns(10);
		// Champ de port
		JLabel lblPort = new JLabel("PORT");
		lblPort.setBounds(200, 23, 38, 16);
		frame.getContentPane().add(lblPort);
		
		portInput = new JTextField();
		portInput.setBounds(235, 18, 62, 26);
		portInput.setDocument(new JTextFieldLimit(4));
		frame.getContentPane().add(portInput);
		portInput.setColumns(10);
		
		JSeparator separator = new JSeparator();
		separator.setBackground(new Color(164,219,251,70));
		separator.setBounds(81, 474, 227, 9);
		frame.getContentPane().add(separator);
		
		// Champ pour le message
		msgInput = new JTextField();
		msgInput.setBackground(new Color(4,170,255));
		msgInput.setForeground(Color.WHITE);
		msgInput.setBounds(83, 453, 217, 28);
		msgInput.setBorder(null);
		msgInput.setColumns(10);
		msgInput.setDocument(new JTextFieldLimit(25));
		msgInput.setText("type a message for server");
		msgInput.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
				msgInput.setText("");
			}
		});
		frame.getContentPane().add(msgInput);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel container = new JPanel();
		container.setBorder(null);
		container.setBackground(new Color(0,128,255));
		container.setLayout(null);
		container.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		container.setPreferredSize(new Dimension(350,60));
		scrollPane.setViewportView(container);
		

		
		scrollPane.setBounds(0, 163, 350, 250);
		frame.getContentPane().add(scrollPane);
		
		JLabel sendBtn = new JLabel("");
		sendBtn.setBounds(306, 453, 38, 30);
		// code exécuté lors du clique sur le bouton d'envoi d'un message au serveur
		sendBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
				// Si aucune @Ip client est diponible pour le moment on affiche un message demandant au client de se connecter avant d'envoyer des messages.
		        if(ip.equals(""))
		        	JOptionPane.showMessageDialog(frame, "First of all Connect.\nThen send the message.", "Error", JOptionPane.ERROR_MESSAGE);
		        //Sinon en envoi le message au serveur
				else
		        { 		
		        	int containerWidth = scrollPane.getPreferredSize().width;
					int containerHeight = scrollPane.getPreferredSize().height;
					
					container.setPreferredSize(new Dimension(containerWidth,containerHeight+90));
					container.revalidate();
					
					JPanel Msg =  new JPanel();
					Client.lastPosition+=44;
					Msg.setBounds(17, Client.lastPosition, 236, 30);
					Msg.setBackground(new Color(0,128,255));
					Msg.setBorder(null);
					Msg.setLayout(null);
					container.add(Msg);
					
					
					JLabel text = new JLabel(msgInput.getText());
					text.setForeground(Color.WHITE);
					text.setBounds(21, 6, 180, 16);
					Msg.add(text);
					
					JLabel msgBg = new JLabel("");
					msgBg.setBounds(0, 0, 262, 30);
					Msg.add(msgBg);
					msgBg.setIcon(new ImageIcon(Client.class.getResource("/blueMsg.png")));
					
		            int i=0,l=0;
		            pw.println(msgInput.getText());
		            // Reception de la réponse du serveur
					try
		               {
		                 String div[] = br.readLine().trim().split("#");
		                 l=div.length;
		                 String response = "";
		                 for(i=0;i<l;i++)
		                  {
		                        response += div[i];
		                  }
		                 JPanel MsgBack =  new JPanel();
							Client.lastPosition+=44;
							MsgBack.setBounds(90, Client.lastPosition, 236, 30);
							MsgBack.setBackground(new Color(0,128,255));
							MsgBack.setBorder(null);
							container.add(MsgBack);
							MsgBack.setLayout(null);
							
							JLabel textBack = new JLabel(response);
							textBack.setBounds(17, 6, 180, 16);
							MsgBack.add(textBack);
							textBack.setForeground(new Color(17,127,246));
							
							JLabel msgBackBg = new JLabel("");
							msgBackBg.setBounds(0, 0, 236, 30);
							MsgBack.add(msgBackBg);
							msgBackBg.setIcon(new ImageIcon(Client.class.getResource("/whiteMsg.png")));
							
							msgInput.setText("");
		               }
		           catch(Exception e1){}
		             
		        }
				
			}
		});
		frame.getContentPane().add(sendBtn);
		// image de derriere plan
		lblLiveChatBg = new JLabel("");
		lblLiveChatBg.setBounds(0, 73, 350, 415);
		lblLiveChatBg.setIcon(new ImageIcon(Client.class.getResource("/liveChat.png")));
		frame.getContentPane().add(lblLiveChatBg);
		
		frame.setVisible(true);
	}
	
	// Classe pour limiter la taille des charactères entrées dans un champ de texte "JTextField"
	class JTextFieldLimit extends PlainDocument {
		   private int limit;
		   JTextFieldLimit(int limit) {
		      super();
		      this.limit = limit;
		   }
		   JTextFieldLimit(int limit, boolean upper) {
		      super();
		      this.limit = limit;
		   }
		   public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		      if (str == null)
		         return;
		      if ((getLength() + str.length()) <= limit) {
		         super.insertString(offset, str,attr);
		      }
		   }
		}
}
