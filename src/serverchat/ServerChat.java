
package serverchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahmed
 */
public class ServerChat extends Thread{
    private List<Conversation> clients = new ArrayList<>();
    private boolean active=true;
    int nbr_client;
    int nombreSectret;
    boolean fin=false;
    String gagnant;
    public static void main(String[] args) {
        // l'appel de la methode start() va executer la methode run()
        // et dans la methode run() on demarre le serveur
        new ServerChat().start();
    }

    @Override
    public void run() {
        ServerSocket ss;
        try {
            ss = new ServerSocket(1232); // tcp
            // generation d'un nombre entre 0 et 60
            nombreSectret = new Random().nextInt(60);
            System.out.println("Demarrage du serveur...");
            while(active){
           Socket socket = ss.accept();
           ++nbr_client;
           Conversation conversation = new Conversation(socket,nbr_client);
           clients.add(conversation);
           conversation.start();                
         }
        } catch (IOException ex) {
            System.err.println("Erreur: "+ex.getMessage());
        }
        
    }
    class Conversation extends Thread{
        int num;
        protected Socket socket;
        public Conversation(Socket socket,int num){
            this.socket = socket; this.num = num;
        }
        public void DiffuserMessage(String message,Socket socket,int numclient){
            for(Conversation client: clients){
                try {
                    if(client.socket!=socket ){
                        if(client.num==numclient || numclient==-1){
                            PrintWriter pw = new PrintWriter(client.socket.getOutputStream(),true);
                            pw.println(message);
                        }
                        
                    }
                } catch (IOException ex) {
                    System.err.println("Erreur "+ex.getMessage());
                }
            }
        }
        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bfr = new BufferedReader(isr);
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os,true);
                System.out.println("Connexion du client numero "+num);
                pw.println("Bienvenue  le client "+num);
                while (true) {
                    String requete = bfr.readLine();
                    if(requete.contains("-->")){
                        String[] params = requete.split("-->");
                        for(String test:params){
                            System.out.println(test);
                        }
                        if(params.length==2){
                            String message = params[1];
                            int numclient = Integer.parseInt(params[0]);
                            DiffuserMessage(message,socket,numclient);
                        }
                    }
                    else{
                        DiffuserMessage(requete,socket,-1);
                    }
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
          }
} 
}
