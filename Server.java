
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Akiva Gubbay
 */
public class Server extends javax.swing.JFrame {

    private int serverPort = 45000; // The port that this server is listening on
    private ServerSocket serverSocket = null;  // Server socket that will listen for incoming connections
    boolean hasStopped = false;
    Vector<ClientHandler> connections = new Vector<ClientHandler>();

    /**
     * Creates new form Server
     */
    public Server() {
        initComponents();
    }
    
    private static String splitMsgFromHeader(String input){
        for(int i = 0; i< input.length();i++){
            if(input.charAt(i) == ':'){
                return input.substring(i+1,input.length());
            }
        }
        return "msg needs to contain \':\'!!! in function splitMsgFromHeader().";
     }

    class ClientHandler extends Thread {

        private Socket clientSocket;
        private Server server;
        private PrintWriter writer;
        private BufferedReader reader;
        String clientName = "";

        public ClientHandler(Socket socket, Server server) {
            this.clientSocket = socket;
            this.server = server;
        }

        public void closeConnection() {
            this.sendToAll("Client " + this.clientName + " disconnected.");
            server.connections.remove(this);
            try {
                clientSocket.close();
                writer.close();
                reader.close();
            } catch (IOException ex) {
                System.out.println("Error: closing socket on server side.");
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        //Maybe synchronized..
//        public void closeAllConnections() {
//            for (int i = 0; i < server.connections.size(); i++) {
//                ClientHandler ch = server.connections.get(i);
//                txtArea_serverLog.append(clientName + "is shutting down.");
//                ch.closeConnection();
//            }
//        }

        //Maybe synchronized..
        //returns true when the recipient name is valid, otherwise, false.
        public boolean sendTo(String recipient, String msg) {
            for(int i = 0; i <server.connections.size(); i++){
                ClientHandler ch = server.connections.get(i);
                String client_name_lower = ch.clientName.toLowerCase();
                String recipient_name_lower = recipient.toLowerCase();
                if(client_name_lower.equals(recipient_name_lower)){
                    ch.writer.println(msg);
                    return true;
                }
            }
            this.writer.println("<System>: "+this.clientName+", there is no online client with the name.\'"+recipient+"\'.");
            return false;
        }
        
        public void sendTo(String msg) {    //This will be called only from sendtoAll().
            writer.println(msg);
        }

        public void sendToAll(String msg) {
            for (int i = 0; i < server.connections.size(); i++) {
                ClientHandler ch = server.connections.get(i);
                ch.sendTo(msg);
            }
        }

        @Override
        public void run() {
            String _connected = " connected.\n", _client = "Client ", del = " ", _is_online = "is online.",recipient_invalid = " attempted messaging offline client.\n",endline = "\n";   //maybe catch '\n' is needed..
            String _forward_msg_from = "Forwarding message from client ", _to = " to ", _all = "all clients.\n", dots = ": ",get_all_names = "get online client names", _disconnected = "has disconnected.";
            String regEx_send_to_all = "send to everyone.*", regEx_send_to_client = "send to .*";
            String regEx_connect_client = "^Connect .*";
            //maybe don't use magic numbers for the indices.. 
            try {
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String msg;
                while (true) {
                    msg = reader.readLine();
                    System.out.println("Server thread: recieved- |" + msg + "|");
       
                    //Case 1: Client is establishing connection.
                    if (msg.matches(regEx_connect_client)) {
                        this.clientName = msg.split(del)[1];    // This is how server obtains the client's name.
                        txtArea_serverLog.append(_client + clientName + _connected);
                        String msg_with_header = clientName + del + _is_online; 
                        this.sendToAll(msg_with_header);
                    }
                    //Case 2: Client sent msg to ALL clients.
                    else if(msg.matches(regEx_send_to_all)){
                        txtArea_serverLog.append(_forward_msg_from + clientName + _to + _all);
                        sendToAll(this.clientName + dots + msg.substring(18,msg.length())); //get rid of magic number..
                    }
                    //Case 3: Client sent msg to a specific client.
                    else if(msg.matches(regEx_send_to_client)){
                        String recipient = msg.split(del)[2];
                        String split_msg_header = splitMsgFromHeader(msg);
                         boolean valid_name = sendTo(recipient, this.clientName + dots + split_msg_header);
                         if(valid_name) txtArea_serverLog.append(_forward_msg_from + clientName + _to + recipient + endline);
                         else txtArea_serverLog.append(_client + clientName + recipient_invalid);
                    }
                    //Case 4: send back all online client names.
                    else if (msg.equals(get_all_names)) {
                        System.out.println("Server: I need to send all client names.");
                        if(server.connections.size() == 0){
                            sendTo("<System>: No online clients.");
                            return;
                        }
                        String client_names = "";
                        int n = server.connections.size();
                        System.out.println("number of online clients: "+n);
                        for (int i = 0; i < n-1; i++) {
                            client_names += server.connections.get(i).clientName+", ";
                        }
                        client_names += server.connections.get(n-1).clientName+".";
                        System.out.println("list of client names: "+client_names);
                        sendTo("<System>: Connected clients - "+client_names);
                    //Case 5: client has disconnected.
                    } else {
                        if (server.serverSocket != null){
                        txtArea_serverLog.append(_client + clientName+del+_disconnected+endline);
                        sendToAll(this.clientName + dots + _disconnected);
                        }
                    }
                    Thread.sleep(1);
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    // I needed this thread otherwise action listeners will not work because always in a while loop.
    class NewClientListener implements Runnable{
        Server server;
        public NewClientListener(Server server){
            this.server = server;
        }
        
        
        @Override
        public void run() {
           while (!hasStopped) { //need to sync 'hasStopped';
            Socket clientSocket = null;  // socket created by accept
            try {
                System.out.println("Server: waiting for clients to connect..");
                clientSocket = this.server.serverSocket.accept(); // wait for a client to connect
                System.out.println("Server: connection created!");
                Thread.sleep(100);
            } catch (IOException e) {
                if (hasStopped) {
                    System.out.println("Error: server was stopped while waiting for clients to connect.\n");
                    return;
                }
            } catch (InterruptedException ex) {
                System.out.println("Error: server was stopped while waiting for clients to connect.\n problen with 'theard.sleep'");
                return;
            }
            //new Thread( new WorkerRunnable(clientSocket, clientInfo)).start();
            ClientHandler ch = new ClientHandler(clientSocket, this.server);
            System.out.println("Server: new client thread starting..");
            ch.start();
            connections.add(ch);
        }
        
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_start = new javax.swing.JButton();
        btn_stop = new javax.swing.JButton();
        txtareaLog = new javax.swing.JScrollPane();
        txtArea_serverLog = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_start.setText("Start");
        btn_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_startActionPerformed(evt);
            }
        });

        btn_stop.setText("Stop");
        btn_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_stopActionPerformed(evt);
            }
        });

        txtArea_serverLog.setEditable(false);
        txtArea_serverLog.setColumns(20);
        txtArea_serverLog.setRows(5);
        txtareaLog.setViewportView(txtArea_serverLog);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(btn_start)
                .addGap(69, 69, 69)
                .addComponent(btn_stop)
                .addContainerGap(107, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtareaLog)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_start)
                    .addComponent(btn_stop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtareaLog, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_startActionPerformed

        this.txtArea_serverLog.append("Server: activated.\n");
        System.out.println("Server: activated.");
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException ex) {
            System.err.println("Error: Cannot listen on this port.\n" + ex.getMessage());
            txtArea_serverLog.append("Error: Cannot listen on this port.\n");
        }
        
        Thread client_listener = new Thread(new NewClientListener(this));
        client_listener.start();
    }//GEN-LAST:event_btn_startActionPerformed

    private void btn_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_stopActionPerformed
        System.out.println("You pressed STOP button.");
        for (int i = 0; i < connections.size(); i++) {
            ClientHandler ch = connections.get(i);
            ch.sendTo("<System>: server has shut down.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            ch.closeConnection();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            System.out.println("Error: problem closing the main server socket in 'btnStopAction()'");
        }
        txtArea_serverLog.append("Server: shut down.");
    }//GEN-LAST:event_btn_stopActionPerformed

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
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new Server().setVisible(true);
                Server server = new Server();
                server.setVisible(true);
                server.setTitle("Server");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_start;
    private javax.swing.JButton btn_stop;
    private javax.swing.JTextArea txtArea_serverLog;
    private javax.swing.JScrollPane txtareaLog;
    // End of variables declaration//GEN-END:variables
}
