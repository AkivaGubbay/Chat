
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
 * Server for the chat. Mannages the clients connections & forwards messages between them.
 */
/**
 *
 * @author Akiva Gubbay
 */
public class Server extends javax.swing.JFrame {
    // Instance variables:
    private int serverPort = 45000; // The port this server is listening on.
    private ServerSocket serverSocket = null;  // Server socket that will listen for incoming connections.
    boolean has_stopped = false;
    Vector<ClientHandler> connections = new Vector<ClientHandler>();
    
    //constants:
    private static final boolean quietlly = true, alertAll = false;
    private static final String empty = "", _connected = " connected.\n", _client = "Client ", del = " ", _forward_msg_from = "Forwarding message from client ";
    private static final String _is_online = "is online.", recipient_invalid = " attempted messaging offline client.\n", endline = "\n", _to = " to ";
    private static final String _all = "all clients.\n", dots = ": ", get_all_names = "get online client names", _disconnected = "has disconnected.";
    private static final String _client_name = "<System>: Client name ", _already_exists = " already exists.\n", failed_connect = "Client failed to connect. Client name occupied.\n";
    private static final String regEx_send_to_all = "send to everyone.*", regEx_send_to_client = "send to .*";
    private static final String regEx_queitly_disconnected = ".*quietley.*", regEx_disconnected = ".*disconnected.*";
    private static final String regEx_connect_client = "^Connect .*";
    
    //Locks for synchronization:
    private Object lock_has_stopped = new Object();

    /**
     * Creates new form Server
     */
    public Server() {
        initComponents();
    }
    
    /**
     * Synchronizes the value of the state of the server.
     * @return state of server (running or stopped).
     */
    private boolean hasStopped(){
        synchronized(lock_has_stopped){
            return has_stopped;
        }
    }
    
    /**
     * Synchronizes the access to the state of the server.
     */
    private void setHasStopped(boolean state){
        synchronized(lock_has_stopped){
            has_stopped = state;
        }
    }
    
    /**
     * splits the message from the client input.
     * @param input data received from client.
     * @return client message without header details.
     */
    private static String splitMsgFromHeader(String input) {
        int msg_begins = input.indexOf(dots) + dots.length();
        int msg_ends = input.length();
        return input.substring(msg_begins, msg_ends);
    }

    /**
     * 
     * @param name of client trying to connect.
     * @return 
     */
    private boolean isNameValid(String name) {
        for (int i = 0; i < connections.size(); i++) {
            String existing_name = connections.get(i).clientName;
            System.out.println("comparing names: " + name + "<->" + existing_name);
            String name_low_case = name.toLowerCase();
            String existing_name_low_case = existing_name.toLowerCase();
            if (name_low_case.equals(existing_name_low_case)) {
                System.out.println("The name " + name + "exits at client - " + i);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Informs all clients about disconnection.
     * Closes all the client sockets.
     * Closes the server's socket.
     */
    private void disconnectServer(){
        setHasStopped(true);    //stops the newClientListener thread.
        btn_start.setEnabled(true);
        //Informing all clients & disconnecting them:
        for (int i = 0; i < connections.size(); i++) {
            ClientHandler ch = connections.get(i);
            if(i == 0) ch.sendToAll("<System>: server has shut down.\n");
            ch.closeConnection(quietlly);
        }
        //Closing server socket:
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            System.out.println("Error: problem closing the main server socket in 'btnStopAction()'");
        }
        try {
                Thread.sleep(2500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        txtArea_serverLog.append("Server: shut down.\n");
        btn_stop.setEnabled(false);
        
    }

    /**
     * instantiate threads that listen to the socket that connects to the client
     * and acts accordingly upon response.
     */
    class ClientHandler extends Thread {
        //Instance variables:
        private Socket clientSocket;
        private Server server;
        private PrintWriter writer;
        private BufferedReader reader;
        String clientName = empty;
        
        //Locks for synchronization:
        private Object lock_writer = new Object();
        
        /**
         * Constructor receiving socket & server.
         * @param socket connected to the client.
         * @param server that manages the chat.
         */
        public ClientHandler(Socket socket, Server server) {
            this.clientSocket = socket;
            this.server = server;
        }
        
        /**
         * Disconnects the client. closes the socket. 
         * @param method whether to alert other online client about disconnecting.
         */
        public void closeConnection(boolean method) {
            //if needed, alert all online clients about disconnecting.
            if(method == alertAll) sendToAll("Client " + this.clientName + " disconnected.");
            server.connections.remove(this);
            //last cliet disconnecting:
            if(server.connections.size() == 0 && !hasStopped()){
                txtArea_serverLog.append("No clients currently online.\n");
                send("<System>: No online clients.");
            }
            try {
                clientSocket.close();
                writer.close();
                reader.close();
            } catch (IOException ex) {
                System.out.println("Error: closing socket on server side.");
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /**
         * Forwards the message to the addressed recipient.
         * @param recipient name of client the message is addressed to.
         * @param msg message being sent.
         * @return 
         */
        public boolean sendTo(String recipient, String msg) {
            for (int i = 0; i < server.connections.size(); i++) {
                ClientHandler ch = server.connections.get(i);
                String client_name_lower = ch.clientName.toLowerCase();
                String recipient_name_lower = recipient.toLowerCase();
                if (client_name_lower.equals(recipient_name_lower)) {
                    ch.send(msg);
                    return true;
                }
            }
            send("<System>: there is no online client with the name.\'" + recipient + "\'.");
            return false;
        }
  
        /**
         * Sends message through the socket. The sending is synchronized.
         * @param msg message being sent.
         */
        private void send(String msg) {
            synchronized(lock_writer){
            writer.println(msg);
            }
        }
        
        /**
         * Sends the given message to all online clients.
         * @param msg message being sent.
         */
        private void sendToAll(String msg) {
            for (int i = 0; i < server.connections.size(); i++) {
                ClientHandler ch = server.connections.get(i);
                ch.send(msg);
            }
        }

        @Override
        public void run() {
            //maybe don't use magic numbers for the indices.. 
            try {
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String msg;
                boolean terminate_connection = false;
                while (!terminate_connection) {
                    msg = reader.readLine();
                    System.out.println("ClientHandler: recieved- |" + msg + "|");
                    //Case 1: Client is establishing connection.
                    if (msg.matches(regEx_connect_client)) {
                        //received: 'Connect [client name]'.
                        String sent_name = "<" + msg.split(del)[1] + ">";    // This is how server obtains the client's name.
                        System.out.println("ClientHandler: Got client name: " + clientName);
                        //checking no online clients occuping that name:
                        if (server.isNameValid(sent_name)) {
                            System.out.println("ClientHandler: Name is valid.");
                            clientName = sent_name;
                            txtArea_serverLog.append(_client + clientName + _connected);
                            String msg_with_header = clientName + del + _is_online;
                            sendToAll(msg_with_header);
                        } else {
                            System.out.println("ClientHandler: Name is NOT valid.");
                            txtArea_serverLog.append(failed_connect);
                            //Telling client that his name is occupied and he must disconnect.
                            send(_client_name + clientName + _already_exists);
                            Thread.sleep(3000); //give time for message to be sent.
                            closeConnection(quietlly);
                            System.out.println("Client handler: killing my self.");
                            terminate_connection = true;
                        }
                    } //Case 2: Client sent msg to ALL clients.
                    else if (msg.matches(regEx_send_to_all)) {
                        //Forwarding message to all online clients.
                        //received: 'send to everyone: msg'.
                        txtArea_serverLog.append(_forward_msg_from + clientName + _to + _all);
                        //Sending msg without header:
                        String split_msg_header = splitMsgFromHeader(msg);
                        sendToAll(this.clientName + dots + split_msg_header);
                    } //Case 3: Client sent msg to a specific client.
                    else if (msg.matches(regEx_send_to_client)) {
                        String recipient = msg.split(del)[2];
                        String split_msg_header = splitMsgFromHeader(msg);
                        boolean valid_name = sendTo(recipient, clientName + dots + split_msg_header);
                        if (valid_name) {
                            txtArea_serverLog.append(_forward_msg_from + clientName + _to + recipient + endline);
                            sendTo(clientName, clientName + dots + split_msg_header);
                            
                        } else {
                            txtArea_serverLog.append(_client + clientName + recipient_invalid);
                        }
                    } //Case 4: send back all online client names.
                    else if (msg.equals(get_all_names)) {
                        System.out.println("ClientHandler: I need to send all client names.");
                        if (server.connections.size() == 0) {
                            send("<System>: No online clients.");
                            return;
                        }
                        String client_names = empty;
                        int n = server.connections.size();
                        System.out.println("ClientHandler: number of online clients: " + n);
                        for (int i = 0; i < n - 1; i++) {
                            client_names += server.connections.get(i).clientName + ", ";
                        }
                        client_names += server.connections.get(n - 1).clientName + ".";
                        System.out.println("ClientHandler: list of client names: " + client_names);
                        send("<System>: Connected clients - " + client_names);
                        //Case 5: client has disconnected.
                    } else if (msg.matches(regEx_disconnected)) {
                            txtArea_serverLog.append(_client + clientName + del + _disconnected + endline);
                            closeConnection(alertAll);
                           terminate_connection = true;
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

    /**
     * Instantiate s thread thats job is to wait on the server's port for new
     * clients. When a new client makes contact the thread creates a socket for
     * communication with that new client.
     */
    class NewClientListener implements Runnable {
        //Instance variables:
        Server server;
        
        /**
         * Constructor receiving server.
         * @param server that manages the chat.
         */
        public NewClientListener(Server server) {
            this.server = server;
        }

        @Override
        public void run() {
            while (!hasStopped()) { //need to sync 'hasStopped';
                Socket clientSocket = null;  // socket created by accept
                try {
                    System.out.println("NewClientListener: waiting for clients to connect..");
                    clientSocket = this.server.serverSocket.accept(); // wait for a client to connect
                    System.out.println("NewClientListener: connection created!");
                    Thread.sleep(100);
                } catch (IOException e) {
                    if (hasStopped()) {
                        System.out.println("Error: server was stopped while waiting for clients to connect.\n");
                        return;
                    }
                } catch (InterruptedException ex) {
                    System.out.println("Error: server was stopped while waiting for clients to connect.\n problen with 'theard.sleep'");
                    return;
                }
                // Creats a thread to wait on new clients socket.
                ClientHandler ch = new ClientHandler(clientSocket, this.server);
                System.out.println("NewClientListener: new client thread starting..");
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
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

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
                .addContainerGap(102, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
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
                .addComponent(txtareaLog, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Starts the server.
     * creates the server socket on server's port.
     * creates thread to listen to the server's socket for new clients.
     * @param evt start button. 
     */
    private void btn_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_startActionPerformed
        setHasStopped(false);
        btn_stop.setEnabled(true);
        btn_start.setEnabled(false);
        this.txtArea_serverLog.append("Server: activated.\n");
        System.out.println("Server: activated.");
        // Creating main server socket on the servers port.
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException ex) {
            System.err.println("Error: Cannot listen on this port.\n" + ex.getMessage());
            txtArea_serverLog.append("Error: Cannot listen on this port.\n");
        }
        //starting thread that will listen to the server's socket for incomming
        // new clients.
        Thread client_listener = new Thread(new NewClientListener(this));
        client_listener.start();
    }//GEN-LAST:event_btn_startActionPerformed
    /**
     * Calls the disconnectServer method.
     * @param evt stop button 
     */
    private void btn_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_stopActionPerformed
        disconnectServer();
    }//GEN-LAST:event_btn_stopActionPerformed
    /**
     * Calls the disconnectServer method.
     * @param evt exiting window. 
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        disconnectServer();
    }//GEN-LAST:event_formWindowClosing

    /**
     * Runs server that manages the chat.
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
                server.btn_stop.setEnabled(false);
                server.setResizable(false);
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
