package reseau;

import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;
import java.io.*;
import java.util.Timer;

/**
 * TCPClient Utilisation: Lancer l'application par la commande java tcpClient
 * <adresse IP du serveur> <port destination> Inserer une ligne et taper
 * "entrer" pour l'envoyer au serveur quitter l'application en tapant "."
 **/

public class Client {

	public final static int SOCKET_PORT = 13267;
	// Pour envoyer le fichier
	static FileInputStream fis = null;
	static BufferedInputStream bis = null;
	static OutputStream os = null;

	// port et adresse
	static int port = 1500;
	static int varInc = 0;
	static InetAddress adresse = null;

	// input-output
	static BufferedReader input;
	static PrintWriter output;

	static String lineToBeSent;

	// socket
	public static Socket socket = null;
	public final static String FILE_TO_SEND = "\\Users\\Enis\\Desktop\\source.txt"; // Pour
																					// windows
	// public final static String FILE_TO_SEND =
	// "/Users/karim/Desktop/source.txt"; // pour
	// Mac
	protected static boolean recu;

	public static void main(String[] args) {

		System.out.println("\n\n*********************************");
		System.out.println("***********Client****************");
		System.out.println("*********************************\n\n");

		int numMenu = choixMenu();
		switch (numMenu) {
		case 1:
			testCnx();
			closeCnx();
			break;
		case 2:
			testCnx();
			break;
		case 3:
			testCnx();
			break;
		case 4:
			break;
		}

		//
		// // si l'adresse et le port sont donnes en argument!!
		// if (args.length == 2) {
		// // adresse
		// try {
		// adresse = InetAddress.getByName(args[0]);
		// } catch (UnknownHostException e) {
		// System.out
		// .println("adresse du serveur = 127.0.0.1 (par defaut)");
		// }
		//
		// // port
		// try {
		// port = Integer.parseInt(args[1]);
		// } catch (Exception e) {
		// System.out.println("port du serveur = 1500 (par defaut)");
		// port = 1500;
		// }
		// }
		//
		// // on assigne l'adresse si ceci n'a pas encore ete fait
		// if (adresse == null) {
		// try {
		// adresse = InetAddress.getByName("127.0.0.1");
		// } catch (UnknownHostException e) {
		// System.out.print(e);
		// System.exit(1);
		// }
		// }
		//
		//

		/*
		 * Envoi du fichier au serveur
		 */

		/*
		 * ETAPE 1 : Decouper le fichier en plusieurs parties de 5byte et les
		 * stockÃ©es dans un ArrayList sous la forme d'objets Tram
		 *
		 * 
		 * try { File myFile = new File(FILE_TO_SEND); ArrayList<Tram>
		 * listTrames = new ArrayList<Tram>(); int tramNbr = 0; int whereToStart
		 * = 0;
		 * 
		 * int tailleFile = (int) myFile.length();
		 * 
		 * System.out .println(
		 * "\nLa taille du fichier a transferer vers le serveurs est de : " +
		 * tailleFile);
		 * 
		 * fis = new FileInputStream(myFile); bis = new
		 * BufferedInputStream(fis);
		 * 
		 * while (tailleFile > 5 && whereToStart + 5 < tailleFile) { // Bouble
		 * // pour // construire // une // TreeMap<key:numerotram, // value: //
		 * les // 5octets> byte[] octetsToSend = new byte[5];
		 * bis.read(octetsToSend, 0, 5); // Pour lire dans le buf et mettre //
		 * juste 5 octete dans // contenuOctets Tram trame = new
		 * Tram(octetsToSend, tramNbr); listTrames.add(trame);
		 * System.out.println(" start from : = " + whereToStart);
		 * 
		 * tramNbr++; whereToStart += 5;
		 * 
		 * }
		 * 
		 * if (whereToStart < tailleFile) { // S'il reste des octet < 5 byte[]
		 * octetsToSend = new byte[tailleFile - whereToStart];
		 * bis.read(octetsToSend, 0, tailleFile - whereToStart); Tram trame =
		 * new Tram(octetsToSend, tramNbr); System.out.println(
		 * " start from : = " + whereToStart + "to " + tailleFile + "");
		 * listTrames.add(trame); }
		 * 
		 * System.out.println(" la taille de la liste des trams a envoyer = " +
		 * listTrames.size());
		 *
		 * 
		 * ETAPE 2 : Envoi des trams stockÃ©es prealablement dans le tableau une
		 * a une 1 Declancher le timer 2 Envoi de la "trame i " au serveur 3
		 * Attendre un Ack avant d'envoyer la trame i+1
		 *
		 * 
		 * Tram ack = null; ObjectOutputStream oos = new ObjectOutputStream(
		 * socket.getOutputStream()); ObjectInputStream ois = new
		 * ObjectInputStream( socket.getInputStream()); for (int i = 0; i <
		 * listTrames.size(); i++) {
		 * 
		 * // Envoi de la tram if (listTrames.get(i).id == 5 && varInc == 0) {
		 * varInc++; System.out.println("la trame 5 n'a pas ete envoyÃ©e !"); }
		 * else { oos.writeObject(listTrames.get(i)); System.out.println(
		 * "Envoi de la trame id = " + listTrames.get(i).id); }
		 * 
		 * // Reception de l'ack
		 * 
		 * try { // recupere l'ACK // try { ack = (Tram) ois.readObject(); // si
		 * c timout l'ack // contiendera toujours // l'ack de la trame //
		 * precedente } catch (SocketTimeoutException ste) { System.out
		 * .println("timout 10000ms! pas de reception d'ack pour la trame :" +
		 * listTrames.get(i).id); } if (ack.getId() == listTrames.get(i).id) //
		 * soit timOut soit // le serveur a // envoyer un // faux ack
		 * System.out.println("Recption de l'ack nÂ° :" + ack.getId()); else {
		 * System.out .println("l'ack de la trame nÂ° : " + listTrames.get(i).id
		 * + " n'a pas ete recu !!! \n Renvoi de la trame: " +
		 * listTrames.get(i).id); i--; } } catch (ClassNotFoundException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 * 
		 * } oos.flush();
		 * 
		 * System.out.println("Done.");
		 * 
		 * Recuperation du MSG de bienvenu envoyÃ© par le serveur * input = new
		 * BufferedReader(new InputStreamReader( socket.getInputStream()));
		 * String message_distant = input.readLine();
		 * System.out.println(message_distant);
		 * 
		 * Recuperation du MSG de bienvenu envoyÃ© par le serveur *
		 * 
		 * } catch (IOException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 * 
		 * // Envoi de message texte au serveur
		 * 
		 * try { // les echanges avec le socket serveur se font e travers impout
		 * et // output input = new BufferedReader(new
		 * InputStreamReader(System.in)); output = new
		 * PrintWriter(socket.getOutputStream(), true);
		 * 
		 * // on envoi le message insere sur console while (true) { lineToBeSent
		 * = input.readLine();
		 * 
		 * // arret si ligne= "." if (lineToBeSent.equals(".")) { break; }
		 * output.println(lineToBeSent); } } catch (IOException e) {
		 * System.out.println(e); }
		 * 
		 * try {
		 * 
		 * System.out.println("fermeture de connexion avec le serveur " +
		 * socket.getInetAddress() + ":" + socket.getPort()); socket.close(); }
		 * catch (IOException e) { System.out.println(e); }
		 */
	}

	public static int choixMenu() {
		System.out.println(
				"******Bienvenu*******\nMenu : \n1 : Tester connexion au serveur\n2 : TransfeÌ�rer un fichier vers le serveur\n3 : Lister le contenu du reÌ�pertoire courant du serveur\n4 : Quitter lâ€™application ");
		System.out.print("  Faite votre choix :");
		return new Scanner(System.in).nextInt();
	}

	public static void testCnx() {

		System.out.print("Donnez l'adresse du serveur : ");
		String adrServ = new Scanner(System.in).nextLine();
		InetAddress adresse = null;
		try {
			adresse = InetAddress.getByName(adrServ);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.print("\nDonnez le port du serveur : ");
		int port = new Scanner(System.in).nextInt();

		System.out.println("\n\nTest de connexion \n \nEtablissement de connexion avec le serveur" + adrServ + ":" + port);

		// connexion au serveur
		try {
			System.out.println("Serveur  " + adresse.getHostAddress() + ":" + port
					+ " est maintenant connecté");
			socket = new Socket(adrServ, port);

			socket.setSoTimeout(5000); // Time out set to 10 seconds

		} catch (UnknownHostException e) {
			System.out.println("\nServeur " + adrServ + ":" + port + " inconnu.");
			return; // Si serveur inconnu, la fonction arrete ici.
		} catch (IOException e) {
			System.out.println("connexion echouee, adresse/port incorrect");
			// erreur, on quitte
			System.exit(1);
		}

		// Reception du message de Bienvenu

		
		Tram msgB = new Tram(null, -3);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while (true) {
			try {
				
				try {
					msgB = (Tram) ois.readObject();
				} catch (SocketTimeoutException ste) {
					System.out
							.println("timout 10000ms! pas de reception du message de bienvenue" );
				}
				if (! (msgB.id == -3)){
					System.out.println("Recevoir bienvenue");
					System.out.println("\t"+ socket.getInetAddress() + ":" + socket.getPort()
							+ " Recu la transmission de la trame " + msgB.id + " (" + msgB.getTabOct().length + " octets)");
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					Tram ackBvn = new Tram(null, msgB.id);
					oos.writeObject(ackBvn);
					System.out.println("\t"+ socket.getInetAddress() + ":" + socket.getPort()
					+ " Acquittement de la transmission de la trame " + msgB.id);
					System.out.println("\t"+ socket.getInetAddress() + ":" + socket.getPort()
					+ " Accepte la transmission de la trame  " + msgB.id+ " (" + msgB.getTabOct().length + " octets)");
					System.out.println(msgB); // Reception du message de bienvenu 
					oos.flush();
					
					break; 
				}


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void closeCnx() {
		try {

			System.out.println(
					"fermeture de connexion avec le serveur " + socket.getInetAddress() + ":" + socket.getPort());
			socket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
