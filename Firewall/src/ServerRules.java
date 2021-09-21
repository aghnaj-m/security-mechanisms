

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;


public class ServerRules extends javax.swing.JFrame {

    /**
     * Creates new form ServerRules
     */
    DefaultListModel listModel = new DefaultListModel();
    JList list = new javax.swing.JList(listModel);
    DefaultListModel listModel2 = new DefaultListModel();
    JList list2 = new javax.swing.JList(listModel2);
    public ServerRules() {
    	setResizable(false);
    	getContentPane().setBackground(new Color(0, 0, 0));
        initComponents();
        this.setVisible(true);
        list.setForeground(new Color(50, 205, 50));
        list.setBounds(100,100,100,200);
        list.setLocation(50, 50);
        list.setVisible(true);
        list2.setForeground(new Color(50, 205, 50));
        list2.setBounds(100,100,100,200);
        list2.setLocation(380, 50);
        list2.setVisible(true);
        getContentPane().add(list);
        getContentPane().add(list2);
        getContentPane().validate();
        getContentPane().repaint();
        this.pack();
        
        try
        {
            FileInputStream fstream = new FileInputStream("block.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   
            {
                if(strLine.equals("XXX.XXX.XXX.XXX")==false)
                {
                    listModel.addElement(strLine);
                }
            }
            in.close();
        }catch(Exception e){}
        
        
        try
        {
            FileInputStream fstream = new FileInputStream("stringblock.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null)   
            {
                    listModel2.addElement(strLine);
            }
            in.close();
        }catch(Exception e){}
        setSize(550,350);
    }


    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel1.setForeground(new Color(50, 205, 50));
        jLabel2 = new javax.swing.JLabel();
        jLabel2.setForeground(new Color(50, 205, 50));
        jPanel1 = new javax.swing.JPanel();
        jPanel1.setBackground(new Color(0, 0, 0));
        jButton1 = new javax.swing.JButton();
        jButton1.setBackground(new Color(0, 0, 0));
        jButton1.setIcon(new ImageIcon(new ImageIcon(ServerRules.class.getResource("/icons8-plus-48.png")).getImage().getScaledInstance(24,24, Image.SCALE_DEFAULT)));
        jButton2 = new javax.swing.JButton();
        jButton2.setBackground(new Color(0, 0, 0));
        jButton2.setIcon(new ImageIcon(new ImageIcon(ServerRules.class.getResource("/icons8-effacer-48.png")).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT)));
        jButton3 = new javax.swing.JButton();
        jButton3.setBackground(new Color(0, 0, 0));
        jButton3.setIcon(new ImageIcon(new ImageIcon(ServerRules.class.getResource("/icons8-annuler-48.png")).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT)));
        jPanel2 = new javax.swing.JPanel();
        jPanel2.setBackground(new Color(0, 0, 0));
        jButton4 = new javax.swing.JButton();
        jButton4.setIcon(new ImageIcon(new ImageIcon(ServerRules.class.getResource("/icons8-plus-48.png")).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT)));
        jButton4.setBackground(new Color(0, 0, 0));
        jButton5 = new javax.swing.JButton();
        jButton5.setBackground(new Color(0, 0, 0));
        jButton5.setIcon(new ImageIcon(new ImageIcon(ServerRules.class.getResource("/icons8-effacer-48.png")).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT)));
        jButton1.setBorder(null);
        jButton2.setBorder(null);
        jButton3.setBorder(null);
        jButton4.setBorder(null);
        jButton5.setBorder(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Server Rules");

        jLabel1.setFont(new Font("Trebuchet MS", Font.BOLD, 14)); 
        jLabel1.setText("Les @ IPs bloqu\u00E9es");

        jLabel2.setFont(new Font("Trebuchet MS", Font.BOLD, 14)); 
        jLabel2.setText("Les mots bloqu\u00E9s");

        jPanel1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        
        lblNewLabel = new JLabel();
        lblNewLabel.setIcon(new ImageIcon(new ImageIcon(ServerRules.class.getResource("/server.png")).getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addGap(182)
        					.addComponent(lblNewLabel))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(52)
        					.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(40)
        					.addComponent(jLabel1)))
        			.addGap(51)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jLabel2))
        			.addGap(151))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGap(19)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jLabel2))
        			.addGap(39)
        			.addComponent(lblNewLabel)
        			.addGap(33)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addGap(178))
        );
        jPanel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        jPanel2.add(jButton4);
        jPanel2.add(jButton5);
        jButton6 = new javax.swing.JButton();
        jButton6.setIcon(new ImageIcon(new ImageIcon(ServerRules.class.getResource("/icons8-annuler-48.png")).getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT)));
        jButton6.setBackground(new Color(0, 0, 0));
        jButton6.setBorder(null);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton6);
        jPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        jPanel1.add(jButton1);
        jPanel1.add(jButton2);
        jPanel1.add(jButton3);
        getContentPane().setLayout(layout);

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
         String input =  JOptionPane.showInputDialog(this ,"Ajouter une addresse IP pour qu'elle soit bloqu�e :");
         Pattern pat = Pattern.compile("[0-2]?[0-9]?[0-9].[0-2]?[0-9]?[0-9].[0-2]?[0-9]?[0-9].[0-2]?[0-9]?[0-9]");
         Matcher mat = pat.matcher(input);
         
         if(mat.matches())
         {
            listModel.addElement(input);
            try
        {
            String filename= "block.txt";
            FileWriter fw = new FileWriter(filename,true);
            fw.write(input + "\n");
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
         }
         else
            JOptionPane.showMessageDialog(rootPane, "Please enter a valid IP Adress");
    }
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
         int selectedIndex = list.getSelectedIndex();
         String str2delete = list.getSelectedValue().toString();
         try {
            BufferedReader file = new BufferedReader(new FileReader("block.txt"));
            String line;String input = "";
            while ((line = file.readLine()) != null) input += line + '\n';
            input = input.replace(str2delete, "XXX.XXX.XXX.XXX"); 
            
            FileOutputStream File = new FileOutputStream("block.txt");
            File.write(input.getBytes());

                } catch (Exception e) {
            System.out.println("Problem reading file.");
         }
         if (selectedIndex != -1) {
                
                listModel.removeElementAt(selectedIndex);
         }
    }
    

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        String input =  JOptionPane.showInputDialog(this ,"Ajouter un mot pour qu'il soit bloqu� :");
       
        listModel2.addElement(input);
        try
        {
            String filename= "stringblock.txt";
            FileWriter fw = new FileWriter(filename,true);
            fw.write(input + "\n");
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex = list2.getSelectedIndex();
         String str2delete = list2.getSelectedValue().toString();
         try {
            BufferedReader file = new BufferedReader(new FileReader("stringblock.txt"));
            String line;String input = "";
            while ((line = file.readLine()) != null) input += line + '\n';
            input = input.replace(str2delete, ""); 
            
            FileOutputStream File = new FileOutputStream("stringblock.txt");
            File.write(input.getBytes());

                } catch (Exception e) {
            System.out.println("Problem reading file.");
         }
         if (selectedIndex != -1) {
                
                listModel2.removeElementAt(selectedIndex);
         }
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private JLabel lblNewLabel;
}
