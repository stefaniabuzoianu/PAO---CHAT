package client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;


@SuppressWarnings("serial")
public class Client extends JFrame implements ActionListener{
	private TextArea primite;
	private TextArea deTrimis;
	private JButton trimite;
	private TextArea online;
	private JFrame frame;
	private MenuBar menubar;
	private Menu conversatie, about;
	private MenuItem exit, rename, sendTo, saveTxt, ab;
	String mesaj = "";
	private String name = "";
	Socket C;
	private DataInputStream in;
	private DataOutputStream out;
	public String [] list;
	public boolean valid(String N){
		if(N.contains("@") || N.contains("~") || N.contains("+")
				|| N.contains(" ") || N.contains("/") || N.contains("\\"))
			return false;
		return true;
	}
	public class THb extends Thread {
		public void run() {
			String aux = "";
			while(true) {
				try {
					aux = in.readUTF();
				} catch (IOException e) {
					System.out.println(e.getLocalizedMessage());
					primite.append("Conexiune inchisa");
					break;
				}
				if(!aux.equals("")) {
					char [] Tst = aux.toCharArray();
					if(Tst[0] != '@') primite.append(aux);
					else {
						list = aux.split("@"); 
						String onl = "";
						for(int i = 1; i < list.length; i++){
							onl = onl + list[i] + "\n";
						}
						online.setText(onl);
					}
				}
			}
		}
	}
	public Client(){
		online = new TextArea(10, 25);
		online.setEditable(false);
		
		final JFrame parent  = new JFrame();
		parent.setDefaultCloseOperation(EXIT_ON_CLOSE);
		boolean ok = false;
		Object ion;
		do {
			ion = JOptionPane.showInputDialog(parent, "Alege nume");
			if (ion != null) {
				name = (String) ion;
				if (!name.isEmpty()){
					if(name.trim().length() > 0 && valid(name)) ok = true;
				}
			} else
				System.exit(JFrame.NORMAL);
		} while (ok == false);
		name = name.trim();
		String em = "";
		try {
			C = new Socket("localhost", 5005);
			in = new DataInputStream(C.getInputStream());
			out = new DataOutputStream(C.getOutputStream());
			out.writeUTF(name);
			em = in.readUTF();
			System.out.println(em);
			if(em.equals("Numele mai exista pe server")){
				JOptionPane.showMessageDialog(parent, ""
						+ "Numele mai exista pe server");
				System.exit(NORMAL);
			}
			else {
				System.out.println(em);
				list = em.split("@");
				System.out.println(list[0] + " " + list[1]);
				String onl = "";
				for(int i = 1; i < list.length; i++){
					onl = onl + list[i] + "\n";
				}
				System.out.println("Onl: " + onl);
				online.setText(onl);
				System.out.println(online.getText());
			}
		} catch (IOException e) {
			
			System.out.println("Nu se conecteaza");
		}
		new THb().start();
		
		frame = new JFrame("Test");
		frame.setSize(600, 450);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new GridBagLayout());
		menubar = new MenuBar();
		
		frame.add(panel);
		frame.setMenuBar(menubar);
		
		GridBagConstraints c = new GridBagConstraints();
		JLabel label1 = new JLabel("Mesaje primite");
		c.gridx = 0;
		c.gridy = 0;
		panel.add(label1, c);
		
		JLabel label2 = new JLabel("Utilizatori:");
		c.gridx = 1;
		c.gridy = 0;
		panel.add(label2);
		
		primite = new TextArea(10, 45);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(primite, c);
		primite.setEditable(false);
		
		
		c.gridx = 1;
		c.gridy = 1;
		panel.add(online, c);
		
		
		deTrimis = new TextArea(10, 45);
		c.gridx = 0;
		c.gridy = 5;
		panel.add(deTrimis, c);
		
		trimite = new JButton("Trimite");
		c.gridx = 0;
		c.gridy = 6;
		panel.add(trimite, c);
				
		conversatie = new Menu("Conversatie");
		menubar.add(conversatie);

		rename = new MenuItem("Schimba nume");
		conversatie.add(rename);

		sendTo = new MenuItem("Vorbeste cu");
		conversatie.add(sendTo);
		
		saveTxt = new MenuItem("Salveaza conversatia");
		conversatie.add(saveTxt);
		
		exit = new MenuItem("Delogare");
		conversatie.add(exit);
		
		about = new Menu("Despre");
		menubar.add(about);
		ab = new MenuItem("Despre program");
		about.add(ab);
		
		
		event e = new event();
		trimite.addActionListener(e);
		
		event1 ev = new event1();
		sendTo.addActionListener(ev);
		
		event2 evv = new event2();
		exit.addActionListener(evv);

		
		rename.addActionListener(this);
		frame.setTitle("Welcome to chat, " + name);
		
		event3 save= new event3();
		saveTxt.addActionListener(save);
		
		event4 desp = new event4();
		ab.addActionListener(desp);
	}
	public class event implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String sz = deTrimis.getText();
			if(!sz.isEmpty()){
			deTrimis.setText("");
			char [] t = sz.toCharArray();
			if(t[0] == '@'){
				try {
					out.writeUTF(sz + "@" + name + "@");
				} catch (IOException ex) {
					System.out.println("Probleme la trimitere");
				}
			}
			else {
				try {
					out.writeUTF(name + ": " + sz + "\n");
				} catch (IOException ex) {
					System.out.println("Probleme la trimitere");
				}
			}
		}
		}
		
	}
	
	public class event2 implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				primite.append("\nLogged out successfully\n");
				trimite.setEnabled(false);
				in.close();
				out.close();
				C.close();
				online.setText("");
			} catch (IOException e1) {
				System.out.println("Nu s-a putut face deconectarea");
			}
			
			
		}
	}
	
	public class event1 implements ActionListener{ // pentru @
		@Override
		public void actionPerformed(ActionEvent e) {
				String cuCine = "";
				final JFrame parent  = new JFrame();
				boolean ok = false;
				Object ion;
				do {
					ion =JOptionPane.showInputDialog(parent,"Alege nume",null);
					if (ion != null) {
						cuCine = (String) ion;
						if (!cuCine.equals("") && valid(cuCine)){
							if(cuCine.trim().length() > 0){
								ok = true;
								deTrimis.setText("@" + cuCine + "@");
							}
						}
					} else
						break;
				} while (ok == false);
		}
	}
	
	public class event3 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFrame parent  = new JFrame();
			String numeFis = JOptionPane.showInputDialog(parent,"Nume fisier",null);
			numeFis +=".txt";
			String buf = primite.getText();
			try{
				PrintWriter pw = new PrintWriter(new File (numeFis));
				pw.write(buf);
				pw.close();
				JOptionPane.showMessageDialog(parent, ""
						+ "conversatie salvata in " + numeFis + ".txt");
			}
			catch(Exception E){
				System.out.println("Probleme cu fisierul");
			}
		}
		
	}
	
	public class event4 implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFrame parent  = new JFrame();
			String buff;
			buff = "Proiect CHAT - Buzoianu Stefania, Nenu Ana-Maria";
			JOptionPane.showMessageDialog(parent, buff, "Despre program", JOptionPane.INFORMATION_MESSAGE);
			
		}
		
	}
	public static void main(String [] Args){
		new Client();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String nickNou = "";
		final JFrame parent  = new JFrame();
		boolean ok = false;
		Object ion;
		do {
			ion = JOptionPane.showInputDialog(parent,"Alege un nou nume",null);
			if (ion != null) {
				nickNou = (String) ion;
				if (!nickNou.equals("") && (nickNou.trim().length() > 0) && valid(nickNou)){
					nickNou = nickNou.trim();
					ok = true;
					boolean gasit = false;
					if(list != null)
					for(int i = 0; i < list.length; i++)
						if(nickNou.equals(list[i])) {gasit = true; break;}
					if(gasit == false)
					try{
						out.writeUTF("~"+nickNou+"~"+name+"~");
						name = nickNou;
						this.frame.setTitle("Welcome to chat, " + name);
						
					}
					catch(Exception E){
						System.out.println("Ceva a mers prost");
					}
					else {
						final JFrame par  = new JFrame();
						String buff;
						buff = "Numele mai exista pe server";
						JOptionPane.showMessageDialog(par, buff, "Alert", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} else
				break;
		} while (ok == false);
    }
}