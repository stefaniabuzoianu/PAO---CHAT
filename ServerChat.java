package serverchat;

import java.util.*; 
import java.net.*; 
import java.io.*; 

public class ServerChat extends Thread{

    private ServerSocket S; 
    private SRM base; 
    private Vector<Client> useri; 
      
    public class Client extends Thread { 
        private DataInputStream in; 
        private DataOutputStream out; 
        private String nick; 
        private boolean online; 
          
        private class Primeste extends Thread{ 
            public void run() { 
                String msg; 
                while (true) { 
                    try{ 
                        msg = in.readUTF(); 
                    } 
                    catch(Exception e){ 
                        online = false; 
                        System.out.println("Utilizator deconectat"); 
                        for(int i = 0; i < useri.size(); i++) 
                            if(useri.get(i).online == false) useri.remove(i); 
                        sendList();
                        break; 
                    } 
                    if(msg.equals("") == false){ 
                        char [] t = msg.toCharArray(); 
                        if(t[0] != '@' && t[0] !='~'){ 
                            direct(msg); 
                            sendList(); 
                            base.set(msg); 
                        } 
                        if(t[0] == '@'){ 
                            direct1(msg); 
                            sendList(); 
                        } 
                        if(t[0] == '~'){ 
                            String nN = ""; 
                            String nV = ""; 
                            String m [] = msg.split("~"); 
                            nN = m[1]; 
                            nV = m[2]; 
                            for(int i = 0; i < useri.size(); i++){ 
                                if(useri.get(i).nick.equals(nV)) useri.get(i).nick = nN; 
                            } 
                            sendList();
                        } 
                          
                    } 
                } 
            } 
        } 
        public Client(Socket c){ 
            Socket conex = c; 
            online = true; 
            try{ 
                in = new DataInputStream(conex.getInputStream()); 
                out = new DataOutputStream(conex.getOutputStream()); 
            } 
            catch(Exception e){ 
                System.out.println("Probleme la conectare"); 
            } 
            try { 
                nick = in.readUTF(); 
            } catch (IOException e) { 
                System.out.println("Nu vrea sa primeasca numele"); 
            } 
              
        } 
  
        public void run(){ 
            Primeste th = new Primeste(); 
            th.start(); 
        } 
          
        synchronized public void sendList(){ 
            String lista = "@"; 
            for(int i = 0; i < useri.size(); i++){ 
                if(useri.get(i).online == true){ 
                    lista = lista + useri.get(i).nick + "@"; 
                } 
            } 
            for(int i = 0; i < useri.size(); i++){ 
                if(useri.get(i).online == true){ 
                    try{ 
                        useri.get(i).out.writeUTF(lista); 
                    } 
                    catch(Exception E){ 
                        System.out.println("Probleme la trimiterea listei"); 
                    } 
                } 
            } 
        } 
        synchronized public void direct(String msg){ 
            for(int i = 0; i < useri.size(); i++){ 
                if(useri.get(i).online == true){ 
                    System.out.println(useri.get(i).nick); 
                    try { 
                        useri.get(i).out.writeUTF(msg); 
                    } 
                    catch(Exception e){ 
                        System.out.println("Probleme la redirectionarea mesajului"); 
                    } 
                } 
            } 
        } 
          
        synchronized public void direct1(String msg){ 
            String cuiTrimit; 
            String ceTrimit; 
            String deLaCine; 
            String [] prel = msg.split("@"); 
            System.out.println(msg); 
            cuiTrimit = prel[1]; 
            ceTrimit = prel[2]; 
            deLaCine = prel[3]; 
            System.out.println(cuiTrimit + " >> " + ceTrimit + " >> " + deLaCine); 
            boolean gasit = false; 
            for(int i = 0; i < useri.size(); i++){ 
                if(useri.get(i).nick.equals(cuiTrimit)){ 
                    if(useri.get(i).online == true){ 
                        gasit = true; 
                        try { 
                            useri.get(i).out.writeUTF(deLaCine + " say: " + ceTrimit + "\n"); 
                        } 
                        catch(Exception e){ 
                            System.out.println("Probleme la redirectionarea mesajului"); 
                        } 
                        base.set(deLaCine + " say: " + ceTrimit); 
                    } 
                } 
                //pentru echo 
                if(useri.get(i).nick.equals(deLaCine)){ 
                    if(useri.get(i).online == true){ 
                        try { 
                            useri.get(i).out.writeUTF("You said to "+ cuiTrimit + ": " + ceTrimit + "\n"); 
                        } 
                        catch(Exception e){ 
                            System.out.println("Probleme la redirectionarea mesajului"); 
                        } 
                        base.set("You said to: "+ cuiTrimit + " " + ceTrimit + "\n"); 
                    } 
                } 
            } 
            if(gasit == false){ 
                for(int j = 0; j<useri.size(); j++){ 
                    if(useri.get(j).online && useri.get(j).nick.equals(deLaCine)){ 
                        try { 
                            useri.get(j).out.writeUTF("Utilizator offline\n"); 
                        } 
                        catch(Exception e){ 
                            System.out.println("Probleme la redirectionarea mesajului"); 
                        } 
                        base.set("Utilizator offline"); 
                    } 
                } 
            } 
        } 
    } 
      
    public ServerChat(){ 
        base = new SRM(); 
        useri = new Vector<>(); 
        try{ 
            S = new ServerSocket(5005, 3600, InetAddress.getByName("localhost")); 
        } 
        catch(Exception e){ 
            System.out.println("Probleme de configurare"); 
        } 
        System.out.println("Serverul functioneaza la parametri normali"); 
    } 
      
    public void run(){ 
        try{ 
            while(true){ 
                Client C = new Client(S.accept()); 
                boolean gasit = false; 
                for(int i = 0; i < useri.size(); i++){ 
                    if(useri.get(i).nick.equals(C.nick)) gasit = true; 
                } 
                if(gasit == false){ 
                    useri.add(C); 
                    C.start(); 
                    String L = "@";
                    for(int i = 0; i < useri.size(); i++)
                    	if(useri.get(i).online) L = L + useri.get(i).nick + "@";
                    for(int i = 0; i < useri.size(); i++)
                    	useri.get(i).out.writeUTF(L);
                    System.out.println(C.nick + " s-a conectat"); 
                } 
                else { 
                    C.out.writeUTF("Numele mai exista pe server"); 
                } 
            } 
        } 
        catch(Exception e){ 
            System.out.println("Probleme de acceptare"); 
        } 
    } 
      
    public static void main(String [] Args){ 
        try { 
            ServerChat Sv = new ServerChat(); 
            Sv.start(); 
        } 
        catch(Exception e){ 
            System.out.println("Probleme la pornirea serverului"); 
        } 
    } 
} 