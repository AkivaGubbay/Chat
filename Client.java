
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.ButtonGroup;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Akiva Gubbay
 */
public class Client extends javax.swing.JFrame {

    private Socket socket = null;       //Socket object for communicating
    private PrintWriter writer = null;    //socket output to server - for sending data through the socket to the server
    private BufferedReader reader = null;  //socket input from server - for reading server's response
    private String name;  //default - local host.
    private String ip = "127.0.0.1"; // Needs to be a built in java IP object..
    private int port = 45000;
    private boolean isConneceted = false;

    /**
     * Creates new form Client
     */
    public Client() {
        initComponents();
    }
    
    private void listenThread() {
        Thread socket_listener = new Thread(new SocketListener());
        socket_listener.start();
    }
    
    
    class SocketListener implements Runnable{

        @Override
        public void run() {
            String regEx_shut_down = ".*shut down.*", regEx_name_exists = ".*already exists.*";
            String data = null;
            try 
            {
                System.out.println("Client: socket thread is waiting.");
                while ((data = reader.readLine()) != null) 
                {
                    txtareaLog.append(data+"\n");
                    System.out.println("socket listener: I recieved - "+data);
                    //Case 1: Server telling client to disconnect. 
                    if(data.matches(regEx_shut_down)){
                        Thread.sleep(1500);
                        disconnect();
                    }
                    //Case 2: Server telling client his name is already in use.
                    else if(data.matches(regEx_name_exists)){
                        txtareaLog.append("<System>: Please connect again with another name.\n");
                        disconnectQuietly();
                    }
                    Thread.sleep(1);
                }
           }catch(Exception ex) { }
        
        }
        
    }
    
    //Disconnecting without alerting other client.
    private void disconnectQuietly() {
        disableButtons();
        isConneceted = false;
        txtName.setText("");
        txtareaMsg.setText("");
        txtClientName.setText("");
        txtAddress.setText("");
        try {
            socket.close();
            reader.close();
            writer.close();
        } catch (Exception e) {
            txtareaLog.append("<Ststem>: Server in currently offline.\n<Ststem>: Please try again later.\n");
            System.out.println("Error: problem Disconneting.");
        }
    }
    
    private void disconnect() {
        disableButtons();
        isConneceted = false;
        if(writer != null) writer.println(this.name + ", has disconnected.");
        txtareaLog.setText("");
        txtareaMsg.setText("");
        txtClientName.setText("");
        txtAddress.setText("");   
        try {
            socket.close();
            reader.close();
            writer.close();
        } catch (Exception e) {
            txtareaLog.append("<Ststem>: Server in currently offline.\n<Ststem>: Please try again later.\n");
            System.out.println("Error: problem Disconneting.");
        }
    }
    
    private void frame_recipientNotChosen(){
        recipientNotChosen.setAlwaysOnTop(true);
        recipientNotChosen.setSize(410, 180);
        recipientNotChosen.setLocationRelativeTo(this);
        recipientNotChosen.setVisible(true);
    }
    
    private void frame_selectWhoSendTo(){
        selectWhoSendTo.setAlwaysOnTop(true);
        selectWhoSendTo.setSize(410, 180);
        selectWhoSendTo.setLocationRelativeTo(this);
        selectWhoSendTo.setVisible(true);
    }
    
    private void frame_noMsgTyped(){
        noMsgTyped.setAlwaysOnTop(true);
        noMsgTyped.setSize(410, 180);
        noMsgTyped.setLocationRelativeTo(this);
        noMsgTyped.setVisible(true);
    }
    
    private void enableButtons(){
        btnClearLog.setEnabled(true);
        btnClearMsg.setEnabled(true);
        btnDissconnect.setEnabled(true);
        btnSend.setEnabled(true);
        btnShowOnline.setEnabled(true);
    }
    
    private void disableButtons(){
        btnClearLog.setEnabled(false);
        btnClearMsg.setEnabled(false);
        btnDissconnect.setEnabled(false);
        btnSend.setEnabled(false);
        btnShowOnline.setEnabled(false);
    }
    
   
    

       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sendingChoiseGroup = new javax.swing.ButtonGroup();
        recipientNotChosen = new javax.swing.JFrame();
        jLabel3 = new javax.swing.JLabel();
        btn_frame_ok = new javax.swing.JButton();
        selectWhoSendTo = new javax.swing.JFrame();
        btn_frame2_ok = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        noMsgTyped = new javax.swing.JFrame();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        btnDissconnect = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnClearLog = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtareaLog = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        btnSend = new javax.swing.JButton();
        btnClearMsg = new javax.swing.JButton();
        radiobtnAllClients = new javax.swing.JRadioButton();
        radiobtnClient = new javax.swing.JRadioButton();
        txtClientName = new javax.swing.JTextField();
        txtareaMsg = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnShowOnline = new javax.swing.JButton();

        recipientNotChosen.setAlwaysOnTop(true);

        jLabel3.setText("Please enter the name of the client you wish to send this message to.");

        btn_frame_ok.setText("OK");
        btn_frame_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_frame_okActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout recipientNotChosenLayout = new javax.swing.GroupLayout(recipientNotChosen.getContentPane());
        recipientNotChosen.getContentPane().setLayout(recipientNotChosenLayout);
        recipientNotChosenLayout.setHorizontalGroup(
            recipientNotChosenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, recipientNotChosenLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
            .addGroup(recipientNotChosenLayout.createSequentialGroup()
                .addGap(165, 165, 165)
                .addComponent(btn_frame_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        recipientNotChosenLayout.setVerticalGroup(
            recipientNotChosenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recipientNotChosenLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_frame_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btn_frame2_ok.setText("OK");
        btn_frame2_ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_frame2_okActionPerformed(evt);
            }
        });

        jLabel4.setText("Please select who you want to send this message to.");

        javax.swing.GroupLayout selectWhoSendToLayout = new javax.swing.GroupLayout(selectWhoSendTo.getContentPane());
        selectWhoSendTo.getContentPane().setLayout(selectWhoSendToLayout);
        selectWhoSendToLayout.setHorizontalGroup(
            selectWhoSendToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectWhoSendToLayout.createSequentialGroup()
                .addGroup(selectWhoSendToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(selectWhoSendToLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel4))
                    .addGroup(selectWhoSendToLayout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(btn_frame2_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        selectWhoSendToLayout.setVerticalGroup(
            selectWhoSendToLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectWhoSendToLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(btn_frame2_ok, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        jLabel6.setText("Please write a message to send.");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout noMsgTypedLayout = new javax.swing.GroupLayout(noMsgTyped.getContentPane());
        noMsgTyped.getContentPane().setLayout(noMsgTypedLayout);
        noMsgTypedLayout.setHorizontalGroup(
            noMsgTypedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(noMsgTypedLayout.createSequentialGroup()
                .addGroup(noMsgTypedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(noMsgTypedLayout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(noMsgTypedLayout.createSequentialGroup()
                        .addGap(143, 143, 143)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        noMsgTypedLayout.setVerticalGroup(
            noMsgTypedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(noMsgTypedLayout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Connect:"));
        jPanel1.setName(""); // NOI18N

        jLabel1.setText("name:");

        txtName.setToolTipText("");
        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        btnConnect.setText("Connect");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        jLabel2.setText("Address:");

        txtAddress.setToolTipText("");
        txtAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAddressActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(btnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConnect)
                    .addComponent(jLabel2)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtName.getAccessibleContext().setAccessibleName("");

        btnDissconnect.setText("Disconnect");
        btnDissconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDissconnectActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Message Log:"));

        btnClearLog.setText("Clear");
        btnClearLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearLogActionPerformed(evt);
            }
        });

        txtareaLog.setEditable(false);
        txtareaLog.setColumns(20);
        txtareaLog.setRows(5);
        jScrollPane3.setViewportView(txtareaLog);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnClearLog)
                .addContainerGap())
            .addComponent(jScrollPane3)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearLog))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Send Message:"));

        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        btnClearMsg.setText("Clear");
        btnClearMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearMsgActionPerformed(evt);
            }
        });

        sendingChoiseGroup.add(radiobtnAllClients);
        radiobtnAllClients.setText("All clients");
        radiobtnAllClients.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobtnAllClientsActionPerformed(evt);
            }
        });

        sendingChoiseGroup.add(radiobtnClient);
        radiobtnClient.setText("Client:");
        radiobtnClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiobtnClientActionPerformed(evt);
            }
        });

        jLabel5.setText("Message: ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(radiobtnAllClients)
                        .addGap(18, 18, 18)
                        .addComponent(radiobtnClient)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClientName, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtareaMsg)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnClearMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radiobtnAllClients)
                    .addComponent(radiobtnClient)
                    .addComponent(txtClientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtareaMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearMsg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnShowOnline.setText("Show Online");
        btnShowOnline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowOnlineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnShowOnline)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDissconnect))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDissconnect)
                    .addComponent(btnShowOnline))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed

    private void txtAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAddressActionPerformed

    private void btnClearLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearLogActionPerformed
        txtareaLog.setText("");
    }//GEN-LAST:event_btnClearLogActionPerformed

    private void btnClearMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearMsgActionPerformed
        txtareaMsg.setText("");
    }//GEN-LAST:event_btnClearMsgActionPerformed

    private void btnDissconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDissconnectActionPerformed
       disconnect();
    }//GEN-LAST:event_btnDissconnectActionPerformed

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        String _connect  = "Connect", del = " ", end_line = "\n";
        if (!isConneceted) {
            enableButtons();
            // Getting Client's name & address from txt field:
            this.name = txtName.getText();
            if(!txtAddress.getText().equals("")) this.ip = txtAddress.getText();
            System.out.println("Client: connetcting to\tname: "+name+"\tip:"+ip);
            //not entering this 'if' when supposed too.
//            if (name.equals("") || ip.equals("")){
//                return;
//            }
            txtClientName.setEditable(false);
            txtAddress.setEditable(false);

            //Astablishing socket with the server:
            try {
                System.out.println("Client: astablishing socket with the server..");
                socket = new Socket(this.ip, this.port);   //establish the socket connection between the client and the server
                writer = new PrintWriter(socket.getOutputStream(), true);  //open a PrintWriter on the socket
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));  //open a BufferedReader on the socket
                isConneceted = true;
                
                System.out.println("Client: sending my name to sever..");
                //Sending this client's name to server.
                writer.println(_connect + del + name);
                //closing 'writer', 'reader' and 'scoket' is done in disconnect(). 
            } catch (UnknownHostException e) {
                this.txtareaLog.append("Don't know about this host\n");
                disconnect();
            } catch (IOException e) {
                this.txtareaLog.append("<System>: the server is currently offline.\n");
                this.txtareaLog.append("<System>: Please try again later.");
                disconnect();
            }
            System.out.println("Client: created socket listening thread.");
            //Creating new thread that will listen to the sockett.
            listenThread();
        }
        else{
            this.txtareaLog.append(this.name + ", You are already conneceted!\n");
        }
    }//GEN-LAST:event_btnConnectActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        String send_to = "send to ", everyone = "everyone",  del = " ", dots = ": ";
        String msg = txtareaMsg.getText();
        //Case 0: Blank message.
        if(msg.equals("")){
            frame_noMsgTyped();
            return;
        }
        //Case 1: 'msg' is to be sent to all clients:
        else if(radiobtnAllClients.isSelected()){
            writer.println(send_to + everyone + dots +msg); 
            txtareaMsg.setText("");
        }
        //Case 2: 'msg' is to be sent to the chosen client:
        else if(radiobtnClient.isSelected()){
            String resipient = "<"+txtClientName.getText()+">";
            //Case 2.1: No resipient was entered.
            if(resipient.equals("<>")){
                frame_recipientNotChosen();
            }
            //Case 2.2: send msg with resipient name.
            else{ 
            writer.println(send_to + resipient+del+dots+ msg);    // if wrong resipient name has been put in, server needs to tell this (sending) client.
            txtareaMsg.setText("");
            }
        }
        //Case 3: No radio buttons were selected.
        else{
            frame_selectWhoSendTo();
        }
        
        // Determin who to send it too.
        
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnShowOnlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowOnlineActionPerformed
        System.out.println("Client: I want a list of online clients please.");
        String get_all_names = "get online client names";
        writer.println(get_all_names);
    }//GEN-LAST:event_btnShowOnlineActionPerformed

    private void radiobtnAllClientsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobtnAllClientsActionPerformed
        txtareaMsg.setEnabled(true);
        txtClientName.setText("");
        txtClientName.setEditable(false);
    }//GEN-LAST:event_radiobtnAllClientsActionPerformed

    private void radiobtnClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiobtnClientActionPerformed
        txtareaMsg.setEnabled(true);
        txtClientName.setEditable(true);
    }//GEN-LAST:event_radiobtnClientActionPerformed

    private void btn_frame_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_frame_okActionPerformed
        recipientNotChosen.setVisible(false);
    }//GEN-LAST:event_btn_frame_okActionPerformed

    private void btn_frame2_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_frame2_okActionPerformed
        selectWhoSendTo.setVisible(false);
    }//GEN-LAST:event_btn_frame2_okActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        noMsgTyped.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        disconnect();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Client client = new Client();
                client.setVisible(true);
                client.setTitle("Client");
                client.btnClearLog.setEnabled(false);
                client.btnClearMsg.setEnabled(false);
                client.btnDissconnect.setEnabled(false);
                client.btnSend.setEnabled(false);
                client.btnShowOnline.setEnabled(false);
                client.setResizable(false);
                //To make the sending choise of sending to all clients or a specific one.
                //This makes it only posible to select one of those choises.
                ButtonGroup sending_group = new ButtonGroup();
                sending_group.add(client.radiobtnClient);
                sending_group.add(client.radiobtnAllClients);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearLog;
    private javax.swing.JButton btnClearMsg;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnDissconnect;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton btnShowOnline;
    private javax.swing.JButton btn_frame2_ok;
    private javax.swing.JButton btn_frame_ok;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JFrame noMsgTyped;
    private javax.swing.JRadioButton radiobtnAllClients;
    private javax.swing.JRadioButton radiobtnClient;
    private javax.swing.JFrame recipientNotChosen;
    private javax.swing.JFrame selectWhoSendTo;
    private javax.swing.ButtonGroup sendingChoiseGroup;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtClientName;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextArea txtareaLog;
    private javax.swing.JTextField txtareaMsg;
    // End of variables declaration//GEN-END:variables

    

    
}
