package reseau;

import java.net.*;
import java.util.ArrayList;
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
	// public final static String FILE_TO_SEND
	// ="\\Users\\Enis\\Desktop\\source.txt"; // Pour windows
	public final static String FILE_TO_SEND = "/Users/karim/Desktop/source.txt"; // pour
																					// Mac
	protected static boolean recu;

	public static void main(String[] args) {
		afficherMenu();

		
		
		
		// Pour envoyer le fichier
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;

		// port et adresse
		int port = 1500;
		int varInc = 0;
		InetAddress adresse = null;

		// socket
		Socket socket = null;

		// input-output
		BufferedReader input;
		PrintWriter output;

		String lineToBeSent;

		System.out.println("\n\n*********************************");
		System.out.println("***********Client****************");
		System.out.println("*********************************\n\n");

		// si l'adresse et le port sont donnes en argument!!
		if (args.length == 2) {
			// adresse
			try {
				adresse = InetAddress.getByName(args[0]);
			} catch (UnknownHostException e) {
				System.out
						.println("adresse du serveur = 127.0.0.1 (par defaut)");
			}

			// port
			try {
				port = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out.println("port du serveur = 1500 (par defaut)");
				port = 1500;
			}
		}

		// on assigne l'adresse si ceci n'a pas encore ete fait
		if (adresse == null) {
			try {
				adresse = InetAddress.getByName("127.0.0.1");
			} catch (UnknownHostException e) {
				System.out.print(e);
				System.exit(1);
			}
		}

		// connexion au serveur
		try {
			System.out.println("Etablissement de connexion vers  \\ "
					+ adresse.getHostAddress() + ":" + port
					+ ", veuillez patienter...");
			socket = new Socket(adresse, port);

			socket.setSoTimeout(10000); // Time out set to 10 seconds

			System.out.println("connecte au serveur " + socket.getInetAddress()
					+ ":" + socket.getPort() + ": inserer du texte a envoyer");

		} catch (UnknownHostException e) {
			System.out.println("\nServeur " + adresse + ":" + port
					+ " inconnu.");
			return; // Si serveur inconnu, la fonction arrete ici.
		} catch (IOException e) {
			System.out.println("connexion echouee, adresse/port incorrect");
			// erreur, on quitte
			System.exit(1);
		}

		/*
		 * Envoi du fichier au serveur
		 */

		/*
		 * ETAPE 1 : Decouper le fichier en plusieurs parties de 5byte et les
		 * stockées dans un ArrayList sous la forme d'objets Tram
		 */

		try {
			File myFile = new File(FILE_TO_SEND);
			ArrayList<Tram> listTrames = new ArrayList<Tram>();
			int tramNbr = 0;
			int whereToStart = 0;

			int tailleFile = (int) myFile.length();

			System.out
					.println("\nLa taille du fichier a transferer vers le serveurs est de : "
							+ tailleFile);

			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);

			while (tailleFile > 5 && whereToStart + 5 < tailleFile) { // Bouble
																		// pour
																		// construire
																		// une
																		// TreeMap<key:numerotram,
																		// value:
																		// les
																		// 5octets>
				byte[] octetsToSend = new byte[5];
				bis.read(octetsToSend, 0, 5); // Pour lire dans le buf et mettre
												// juste 5 octete dans
												// contenuOctets
				Tram trame = new Tram(octetsToSend, tramNbr);
				listTrames.add(trame);
				System.out.println(" start from : = " + whereToStart);

				tramNbr++;
				whereToStart += 5;

			}

			if (whereToStart < tailleFile) { // S'il reste des octet < 5
				byte[] octetsToSend = new byte[tailleFile - whereToStart];
				bis.read(octetsToSend, 0, tailleFile - whereToStart);
				Tram trame = new Tram(octetsToSend, tramNbr);
				System.out.println(" start from : = " + whereToStart + "to "
						+ tailleFile + "");
				listTrames.add(trame);
			}

			System.out.println(" la taille de la liste des trams a envoyer = "
					+ listTrames.size());

			/*
			 * ETAPE 2 : Envoi des trams stockées prealablement dans le tableau
			 * une a une 1 Declancher le timer 2 Envoi de la "trame i " au
			 * serveur 3 Attendre un Ack avant d'envoyer la trame i+1
			 */

			Tram ack = null;
			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());
			for (int i = 0; i < listTrames.size(); i++) {

				// Envoi de la tram
				if (listTrames.get(i).id == 5 && varInc == 0) {
					varInc++;
					System.out.println("la trame 5 n'a pas ete envoyée !");
				} else {
					oos.writeObject(listTrames.get(i));
					System.out.println("Envoi de la trame id = "
							+ listTrames.get(i).id);
				}

				// Reception de l'ack

				try {
					// recupere l'ACK //
					try {
						ack = (Tram) ois.readObject(); // si c timout l'ack
														// contiendera toujours
														// l'ack de la trame
														// precedente
					} catch (SocketTimeoutException ste) {
						System.out
								.println("timout 10000ms! pas de reception d'ack pour la trame :"
										+ listTrames.get(i).id);
					}
					if (ack.getId() == listTrames.get(i).id) // soit timOut soit
																// le serveur a
																// envoyer un
																// faux ack
						System.out.println("Recption de l'ack n° :"
								+ ack.getId());
					else {
						System.out
								.println("l'ack de la trame n° : "
										+ listTrames.get(i).id
										+ " n'a pas ete recu !!! \n Renvoi de la trame: "
										+ listTrames.get(i).id);
						i--;
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			oos.flush();

			System.out.println("Done.");

			/* Recuperation du MSG de bienvenu envoyé par le serveur */
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String message_distant = input.readLine();
			System.out.println(message_distant);

			/* Recuperation du MSG de bienvenu envoyé par le serveur */

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Envoi de message texte au serveur

		try { // les echanges avec le socket serveur se font e travers impout et
				// output
			input = new BufferedReader(new InputStreamReader(System.in));
			output = new PrintWriter(socket.getOutputStream(), true);

			// on envoi le message insere sur console
			while (true) {
				lineToBeSent = input.readLine();

				// arret si ligne= "."
				if (lineToBeSent.equals(".")) {
					break;
				}
				output.println(lineToBeSent);
			}
		} catch (IOException e) {
			System.out.println(e);
		}

		try {

			System.out.println("fermeture de connexion avec le serveur "
					+ socket.getInetAddress() + ":" + socket.getPort());
			socket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void afficherMenu() {
		System.out
				.println("******Bienvenu*******\nMenu : \n1 : Tester connexion au serveur\n2 : Transférer un fichier vers le serveur\n3 : Lister le contenu du répertoire courant du serveur\n4 : Quitter l’application\n  Faite votre choix :");

	}

}
