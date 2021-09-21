
import java.awt.Color;
import java.net.*;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import javax.swing.JLabel;


public class ServerWindow extends javax.swing.JFrame{

	/*
	 * Class variables declaration
	 */
    static String confirmation="";
    static String print="";
    static String confirm="";
    static Thread t=Thread.currentThread();
    

    public ServerWindow() {
    	// set background color to black
    	getContentPane().setBackground(new Color(0, 0, 0));
    	// call components initialization method
        initComponents();
        // set a position
        this.setLocation(770, 0); 
        this.setVisible(true);
       
        while(!t.isInterrupted())
        {
                if(confirm.equals(confirmation)==false)
                {
                    jTextArea1.append(confirmation);
                    jTextArea2.append(print);
                    confirm=confirmation;
                    print="";
                }
        }
    }
    
    public static void getWelcomeMessage(String args[])
    {
        
        confirmation="Welcome! This server is running on IP " + args[0] + "\nand port " + args[1] + "\nWaiting for Connection..\n";
        
    }
    
    public static void getConfirmation(String args[])       
    {    
        if(args[0].contains("Blocked")==false)
        {
            confirmation="Connection Established from IP " + args[0] + "\n";
            print=args[0] + "\n";
        }
        else
        {
            confirmation="Connection from IP " + args[0] + "\n";
            print=args[0] + "\n";
        }
        try
        {
            String filename= "log.txt";
            FileWriter fw = new FileWriter(filename,true); 
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd\tHH:mm:ss");
            Calendar cal = Calendar.getInstance();
            fw.write(args[0] + "\t\t" + dateFormat.format(cal.getTime()) + "\n");
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    
    public static void getClientMessage(String args[])
    {
        
        confirmation="The client " + args[1] + " has sent " + args[0] + "\n";
        
    }
    @SuppressWarnings("unchecked")
    
    private void initComponents() {

        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextArea1.setForeground(new Color(50, 205, 50));
        jTextArea1.setBackground(new Color(0, 0, 0));
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jTextArea2.setForeground(new Color(50, 205, 50));
        jTextArea2.setBackground(new Color(0, 0, 0));
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuBar1.setForeground(new Color(0, 0, 0));
        jMenuBar1.setBackground(new Color(50, 205, 50));
        jMenu1 = new javax.swing.JMenu();
        jMenu1.setForeground(new Color(255, 255, 255));
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu2.setForeground(new Color(255, 255, 255));
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenu5.setForeground(new Color(255, 255, 255));
        jMenuItem4 = new javax.swing.JMenuItem();

        jMenuItem3.setText("jMenuItem3");

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server 127.0.0.1 Port : 7344");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "Server Report", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Calibri", 1, 14))); 
        jScrollPane1.setViewportView(jTextArea1);

        jTextArea2.setColumns(10);
        jTextArea2.setRows(5);
        jTextArea2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2), "State Table", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Calibri", 1, 14))); 
        jScrollPane2.setViewportView(jTextArea2);

        jMenuBar1.setBorder(null);
        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jMenuBar1.setFont(new java.awt.Font("Segoe UI", 0, 48)); 

        jMenu1.setText("Server");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Stop & Quit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Rules");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Server Rules");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        jMenu5.setText("View");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("View Log");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem4);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
        		.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(layout.createSequentialGroup()
        			.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 268, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        getContentPane().setLayout(layout);

        pack();
    }

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        ServerRules sr = new ServerRules();
    }

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {
        try{
        LogWindow l = new LogWindow();
        }
        catch(Exception e){}
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
    }
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
}
