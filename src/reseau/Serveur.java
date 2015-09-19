package reseau;



// tcpServer.java by fpont 3/2000

// usage : java tcpServer <port number>.
// default port is 1500.
// connection to be closed by client.
// this server handles only 1 connection.

import java.net.*;
import java.io.*;

/**  TCPServeur
 **  Utilisation:
 **  Lancer l'application par la commande java tcpServeur <port d'ecoute>
 **  Le serveur reste en attente d'un client. Quand la connexion est établie, 
 **  il va afficher les messages envoyées par ce client
 **	 quitter l'application avec ctrl+C
 **/

public class Serveur 
{
    
    public static void main(String args[]) 
	{
	
		int port=1500;
		ServerSocket socket_serveur;
		BufferedReader input;
		
		
		System.out.println("\n\n*********************************");
		System.out.println("***********Serveur***************");
		System.out.println("*********************************\n\n");
		// si le port est donné en argument!!
		if(args.length == 1) 
		{
			try 
			{ 
				port = Integer.parseInt(args[0]);
			}
			catch (Exception e) {
				System.out.println("port d'ecoute= 1500 (par defaut)");
				port = 1500;
			}
		}
		
		
		//Ouverture du socket en attente de connexions
		try 
		{
			socket_serveur = new ServerSocket(port);
			System.out.println("Serveur en attente de clients sur le port " + 
					   socket_serveur.getLocalPort());
			
			// boucle infinie: traitement d'une connexion client
			while(true) 
			{
				Socket socket = socket_serveur.accept();
				System.out.println("nouvelle connexion acceptee " +
						   socket.getInetAddress() +
						   ":" + socket.getPort());
				input = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
				
				// imprimer le texte reçu 
				try 
				{
					while(true) 
					{
						String message = input.readLine();
						if (message==null) 
							break;
						System.out.println(message);
					}
				}
				catch (IOException e) 
				{
					System.out.println(e);
				}
			
				// connexion fermée par client
				try 
				{
					socket.close();
					System.out.println("connexion fermée par le client");
				}
				catch (IOException e) {
					System.out.println(e);
				}
			}
			
			
		}
		catch (IOException e) 
		{
			System.out.println(e);
		}
    }
}

