package Example;

import java.net.*;
import java.io.*;

/**  TCPClient
 **  Utilisation:
 **  Lancer l'application par la commande java tcpClient <adresse IP du serveur> <port destination>
 **  Inserer une ligne et taper "entrer" pour l'envoyer au serveur
 **	 quitter l'application en tapant "."
 **/

public class tcpClient 
{
	
	
	public static void main(String[] args) 
	{
		//port et adresse
		int port=1500;
		InetAddress adresse=null;
		BufferedReader in;
		PrintWriter out;
		
		//socket
		Socket socket = null;

		//input-output
		BufferedReader input;
		PrintWriter output;
		
		
		String lineToBeSent;
	
		System.out.println("\n\n*********************************");
		System.out.println("***********Client****************");
		System.out.println("*********************************\n\n");
		
		
		// si l'adresse et le port sont donnes en argument!!
		if(args.length == 2) 
		{
			//adresse
			try 
			{ 
				adresse = InetAddress.getByName(args[0]); // Premier argument contient l'adresse du serveur 
			}
			catch (UnknownHostException e) 
			{
				System.out.println("adresse du serveur = 127.0.0.1 (par defaut)");
			}
			
			//port
			try 
			{ 
				port = Integer.parseInt(args[1]); // Port dans le deuxiem argument 
			}
			catch (Exception e) 
			{
				System.out.println("port du serveur = 1500 (par defaut)");
				port = 1500;
			}
		}

		// on assigne l'adresse si ceci n'a pas encore �t� fait
		if(adresse==null)
		{
			try
			{
				adresse = InetAddress.getByName("127.0.0.1");
			}
			catch(UnknownHostException e) 
			{
				System.out.print(e);
				System.exit(1);
			}
		}
		
		
		//connexion au serveur
		try 
		{
			System.out.println("Etablissement de connexion vers  \\ " +
					   adresse.getHostAddress()+
					   ":" + port+ ", veuillez patienter...");
			
		        
			socket = new Socket(adresse, port);// doivent etre passés en arguments
			System.out.println("connecte au serveur " +
					   socket.getInetAddress()+
					   ":" + socket.getPort() );
			
			
			
			/* Recuperation du MSG de bienvenu envoyé par le serveur */   
			in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
		        String message_distant = in.readLine();
		        System.out.println(message_distant);
		        
		    /* Recuperation du MSG de bienvenu envoyé par le serveur */ 
		        
		        
		    System.out.println("\n inserer du texte a envoyer");
		        
		        
		
		} catch (UnknownHostException e) {
			System.out.println("\nServeur " + adresse +":"+ port + " inconnu.");
			return; // Si serveur inconnu, la fonction arrete ici.
		}
		catch (IOException e) 
		{
			System.out.println("connexion echouee, adresse/port incorrect");
			//erreur, on quitte
			System.exit(1);
		}
	
		
		// == connexion reussie == // 
		//Envoi de message texte au serveur
		try 
		{
			//les echanges avec le ssocket serveur se font a travers impout et output
			input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("karim Testing : "+input.readLine());
			output = new PrintWriter(socket.getOutputStream(),true); // 
			
			// on envoi le message insere sur console
			while(true) 
			{
				lineToBeSent = input.readLine();
				
				if(lineToBeSent.equals("."))
				{
					break; // Break pour sortir de la boucle et fermer la connexion 
				}
				
				output.println(lineToBeSent);
				System.out.println("Test : "+lineToBeSent);
				
			}
		}
		catch (IOException e) 
		{
			System.out.println(e);
		}

		try 
		{
			System.out.println("fermeture de connexion avec le serveur " +
							socket.getInetAddress()+
							":" + socket.getPort());
			
			/* Recuperation du MSG de Fermeture envoyé par le serveur */   
			in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
		        String message_distant = in.readLine();
		        System.out.println(message_distant);
		        
		    /* Recuperation du MSG de Fermeture envoyé par le serveur */ 
			socket.close();
		}
		catch (IOException e) 
		{
			System.out.println(e);
		}
	}
}