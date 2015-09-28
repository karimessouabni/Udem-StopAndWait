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

	// public final static String FILE_TO_RECEIVED ="\\Users\\Enis\\Desktop\\Sourcetelechargee.txt";
	public final static String FILE_TO_RECEIVED = "/Users/karim/Desktop/Sourcetelechargee.txt";

	public static void main(String args[]) {

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		int bytesRead;
		int current = 0; 
		int varInc = 0 ;  // A decrementer pour simuler la perte de la meme trame pour son deuxiem envoi	

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
					Tram trame;
					Tram ack;

					while ((trame = (Tram) ois.readObject()) != null) {
						System.out.print("trame n° " + trame.id + " du fichier : "
								+ FILE_TO_RECEIVED + " telechargée  ("
								+ trame.tabOct.length + "bytes read)");
						
						if(!listTrames.isEmpty()){ // si ce n'est pas la premiere trame
							if (listTrames.get(listTrames.size()-1).id == trame.id-1  ){ // si c'est la trame qui suit celle recue prealabment
								System.out.println(" <= Données acceptées ");
								listTrames.add(trame);
								
							}else {
								System.out.println(" <= Données refusées ");
							}
							
						}
						else {
							if( trame.id==0){
								System.out.println(" <= Données acceptées ");
								listTrames.add(trame);
							}else {
								System.out.println(" <= Données refusées ");
							}
							
						}
						
						// Données acceptées ou refusées il faut envoyer un ACK !!! 

						// envoie ACK //
						ack = new Tram(null, trame.id);// Envoi une trame vide
														// de 0 octets mais avec
														// l'id de la trame
														// d'avant
						Timer timer = new Timer();
						if (ack.id == 2 && varInc == 0 ) {
							varInc++ ; 

						} else {
							oos.writeObject(ack);
							System.out.println("Envoi de l'ack pour la trame "+trame.id);
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
