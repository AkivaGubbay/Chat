/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Akiva Gubbay
 */
public class ChatConstant {
    
    //Shared
    static final boolean quietlly = true, alertAll = false;
    static final String empty = "", del = " ", dots = ": ", get_all_names = "get online client names";
    // Server
    static final String _connected = " connected.\n", _client = "Client " , _forward_msg_from = "Forwarding message from client ";
    static final String _is_online = "is online.", recipient_invalid = " attempted messaging offline client.\n", endline = "\n", _to = " to ";
    static final String _all = "all clients.\n", _disconnected = "has disconnected.";
    static final String _client_name = "<System>: Client name ", _already_exists = " already exists.\n", failed_connect = "Client failed to connect. Client name occupied.\n";
    static final String regEx_send_to_all = "send to everyone.*", regEx_send_to_client = "send to .*";
    static final String regEx_queitly_disconnected = ".*quietley.*", regEx_disconnected = ".*disconnected.*";
    static final String regEx_connect_client = "^Connect .*";
    
    //Clinet
    static final String  _connect = "Connect", has_disconnected = ", has disconnected.";
    static final String send_to = "send to ", everyone = "everyone";
}
