package reseau;

// tcpServer.java by fpont 3/2000

// usage : java tcpServer <port number>.
// default port is 1500.
// connection to be closed by client.
// this server handles only 1 connection.

import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

/**
 * TCPServeur Utilisation: Lancer l'application par la commande java tcpServeur
 * <port d'ecoute> Le serveur reste en attente d'un client. Quand la connexion
 * est etablie, il va afficher les messages envoyees par ce client quitter
 * l'application avec ctrl+C
 **/

public class Serveur {

	private static final int FILE_SIZE = 6022386;

	// public final static String FILE_TO_RECEIVED =
	// "\\Users\\Enis\\Desktop\\Sourcetelechargee.txt";
	public final static String FILE_TO_RECEIVED = "/Users/karim/Desktop/Sourcetelechargee.txt";

	public static void main(String args[]) {

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		int bytesRead;
		int current = 0;

		int port = 1500;
		ServerSocket socket_serveur;
		BufferedReader input;
		PrintWriter out; // Ajouté

		System.out.println("\n\n*********************************");
		System.out.println("***********Serveur***************");
		System.out.println("*********************************\n\n");
		// si le port est donne en argument!!
		if (args.length == 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("port d'ecoute= 1500 (par defaut)");
				port = 1500;
			}
		}

		// Ouverture du socket en attente de connexions
		try {
			socket_serveur = new ServerSocket(port);
			System.out.println("Serveur en attente de clients sur le port "
					+ socket_serveur.getLocalPort());

			// boucle infinie: traitement d'une connexion client
			while (true) {
				Socket socket = socket_serveur.accept();
				System.out.println("nouvelle connexion acceptee "
						+ socket.getInetAddress() + ":" + socket.getPort());
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				// timeout a 1000
			

				// Reception d'un Fichier
				/*
				 * try {
				 * 
				 * byte[] mybytearray = new byte[FILE_SIZE]; InputStream is; is
				 * = socket.getInputStream();
				 * 
				 * fos = new FileOutputStream(FILE_TO_RECEIVED); bos = new
				 * BufferedOutputStream(fos); bytesRead = is.read(mybytearray,
				 * 0, mybytearray.length); current = bytesRead;
				 * 
				 * bos.write(mybytearray, 0, current);
				 * System.out.println("File " + FILE_TO_RECEIVED +
				 * " downloaded (  bytes read)"); bos.flush();
				 * 
				 * 
				 * // } // else out = new PrintWriter(socket.getOutputStream());
				 * //
				 * out.println(" \t=== Evnoi de tram reussie ! ("+current+" octets)"
				 * ); // out.flush();
				 * 
				 * } catch (IOException e1) { // TODO Auto-generated catch block
				 * e1.printStackTrace(); }
				 */

				/*
				 * 
				 * Reception des Trames envoyées par le client 1 Rassembler le
				 * tout dans un tableau
				 */

				ArrayList<Tram> listTrames = new ArrayList<Tram>();
				try {
					ObjectInputStream ois = new ObjectInputStream(
							socket.getInputStream());
					ObjectOutputStream oos = new ObjectOutputStream(
							socket.getOutputStream());
					String str;
					Tram trame;
					Tram ack;

					while ((trame = (Tram) ois.readObject()) != null) {
						System.out.println("\n id Trame recu = " + trame.id);
						listTrames.add(trame);

						// envoie ACK //
						ack = new Tram(null, trame.id);// Envoi une trame vide
														// de 0 octets mais avec
														// l'id de la trame
														// d'avant
						Timer timer = new Timer();
						if (ack.id == 6) {

							timer.schedule(new TimerTask() {
								@Override
								public void run() {
									try {
										Tram ack = new Tram(null, 6);
										System.out
												.println("\nEnvoie de l'ack 6");
										oos.writeObject(ack);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}, 15000);

							// try {
							// // Thread.sleep(2000); // Attente de 2 sec ->
							// avant la fin du timer
							// // Thread.sleep(4000); // Attente de 4 sec ->
							// apres la fin de timer -> renvoi !!!!
							// oos.writeObject(ack);
							// } catch (InterruptedException ex) {
							// Thread.currentThread().interrupt();
							// }
							// Envoi de l'ack apres 2 secondes

						} else {
							oos.writeObject(ack);
							// ack.setTabOct("OK".getBytes()); // Pourquoi faire
							// ??
						}

						if (trame.id == 8)
							break; // le break est a refaire dynamiquement

						oos.flush();
					}

				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				/*
				 * ETAPE 2 : parcourir le tableau contenant les trames pour les
				 * rasesmbler
				 */

				System.out.println("Etap 2 ");

				fos = new FileOutputStream(FILE_TO_RECEIVED);
				bos = new BufferedOutputStream(fos);

				for (Tram trame : listTrames) {

					bos.write(trame.tabOct, 0, trame.tabOct.length); // ecrire
																		// les 5
																		// bytes
																		// de la
																		// trame
																		// i (
																		// ou
																		// moins
																		// pour
																		// la
																		// derniere)
																		// dans
																		// bos
					bos.flush();
					System.out.println("trame n° " + trame.id + " du ficier : "
							+ FILE_TO_RECEIVED + " downloaded ("
							+ trame.tabOct.length + "bytes read)");

				}

				/*
				 * // imprimer le texte recu try { while (true) { String message
				 * = input.readLine(); if (message == null) break;
				 * System.out.println(message); } } catch (IOException e) {
				 * System.out.println(e); }
				 */
				// connexion fermee par client
				try {
					socket.close();
					System.out.println("connexion fermee par le client");
				} catch (IOException e) {
					System.out.println(e);
				}
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
