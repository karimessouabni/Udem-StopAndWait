package reseau;

// tcpServer.java by fpont 3/2000

// usage : java tcpServer <port number>.
// default port is 1500.
// connection to be closed by client.
// this server handles only 1 connection.

import java.net.*;
import java.io.*;

/**
 * TCPServeur Utilisation: Lancer l'application par la commande java tcpServeur
 * <port d'ecoute> Le serveur reste en attente d'un client. Quand la connexion
 * est �tablie, il va afficher les messages envoy�es par ce client quitter
 * l'application avec ctrl+C
 **/

public class Serveur {

	private static final int FILE_SIZE = 6022386;
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
		// si le port est donn� en argument!!
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

				// Reception d'un Fichier
				try {

					
					byte[] mybytearray = new byte[FILE_SIZE];
					InputStream is;
					is = socket.getInputStream();

					fos = new FileOutputStream(FILE_TO_RECEIVED);
					bos = new BufferedOutputStream(fos);
					bytesRead = is.read(mybytearray, 0, mybytearray.length);
					current = bytesRead;

					bos.write(mybytearray, 0, current);
					System.out.println("File " + FILE_TO_RECEIVED
							+ " downloaded (  bytes read)");
					bos.flush();
					
					
				
					
//				}
//				else 	out = new PrintWriter(socket.getOutputStream());
//			        out.println(" \t=== Evnoi de tram reussie ! ("+current+" octets)");
//			        out.flush();
			        

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
		/*		
				// imprimer le texte re�u
				try {
					while (true) {
						String message = input.readLine();
						if (message == null)
							break;
						System.out.println(message);
					}
				} catch (IOException e) {
					System.out.println(e);
				}
*/
				// connexion ferm�e par client
				try {
					socket.close();
					System.out.println("connexion ferm�e par le client");
				} catch (IOException e) {
					System.out.println(e);
				}
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
