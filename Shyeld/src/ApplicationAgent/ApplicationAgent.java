package ApplicationAgent;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import Db.SuperHero;
import Db.Util;
import Db.Combat;
import Db.DbAgent;
import Db.Participation;
import Db.Reperage;

public class ApplicationAgent {
	
	private java.util.Scanner scanner = new java.util.Scanner(System.in); 
	private DbAgent connexionDb = new DbAgent();
	private int idAgent;
	private boolean estConnecte;


	public ApplicationAgent() {
		super();
	}

	public void menuPrincipal() {
		try {
			do {
				System.out.println("--------------------------------------");
				System.out.println(" Bienvenue dans l'Agent App");
				System.out.println("--------------------------------------");
				System.out.println("1. Informations sur un Superhero");
				System.out.println("2. Ajouter son rapport au sujet d'un combat");
				System.out.println("3. Ajouter un Reperage");
				System.out.println("4. Signaler la mort d'un Superhero");
				System.out.println("5. Quitter l'application");
				
				int choix;
				choix = scanner.nextInt();
	
				switch(choix){
				case 1 :
					informationSuperHero();
					break;
				case 2 :
					rapportCombat();
					break;
				case 3 : 
					reperage();
					break;
				case 4 :
					signalerDecesSH();
					break;
				case 5:
					System.exit(0);
				default :	
					System.out.println("Mauvais chiffre entr�, faites attention la prochaine fois");
				}
				System.out.println("Voulez vous continuer (O/N)");
			} while(Util.lireCharOouN(scanner.next().charAt(0)));
		} catch (InputMismatchException im){
			System.out.println("Attention � votre �criture !");
			scanner = new java.util.Scanner(System.in);
			this.menuPrincipal();
		}
	}

	public void connexion() {
		try {
			System.out.println("-----------------------------------------");
			System.out.println("Bienvenue dans la fen�tre de connexion");
			System.out.println("-----------------------------------------");
			System.out.println("1. Se connecter");
			System.out.println("2. Quitter l'application");
			do {
				int choixLogin = scanner.nextInt();
				switch(choixLogin) {
				case 1 :
					login();
					break;
				case 2 :
					System.exit(0);
				}
			} while(!estConnecte);
		} catch (InputMismatchException im) {
			System.out.println("Faites attention � votre �criture !");
			scanner = new java.util.Scanner(System.in);
			this.connexion();
		}
	}
	private void login(){
		while(true){
			System.out.println("Entrez votre identifiant : ");
			String identifiant = scanner.next();
			System.out.println("Entrez votre mot de passe : ");
			String mdpClair = scanner.next();
			String mdpHashed = connexionDb.checkConnexion(identifiant);
			if(mdpHashed != null && Util.verifPasswordBcrypt(mdpClair, mdpHashed)) {
				try {
					this.idAgent = connexionDb.getAgent(identifiant);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				estConnecte = true;
				return;
			}
			System.out.println("Mauvais identifiants !");
		}
		
	}
		
	
	
	public void informationSuperHero(){
		System.out.println("Veuilliez entrer le nom de votre superhero : ");
		String nom = scanner.next();
		SuperHero superHero = connexionDb.informationSuperHero(nom);
		if(superHero != null) {
			/*for(SuperHero superHero: listeSuperHero) {
				System.out.println(superHero.toString());
			}*/
		} else {
			System.out.println("Aucun H�ros ne correspond au nom entr�, le processus d'inscription est lanc� : ");
			creationSuperHero(nom);
		}
	}
	
	public int creationSuperHero(String nomSuperHero) {
		System.out.println("Veuilliez entrer le nom civil du superh�ros : ");
		String nom = scanner.next();
		System.out.println("Veuilliez entrer le prenom civil du superh�ros");
		String prenom = scanner.next();
		if(nomSuperHero == null) {
			System.out.println("Veuilliez entrer le surnom : ");
			nomSuperHero = scanner.next();
		}
		System.out.println("Entrer l'adresse du superhero : ");
		String adresse = scanner.next();
		System.out.println("Entrer l'origine du superh�ros : ");
		String origine = scanner.next();
		System.out.println("Entrer le type de super pouvoir qu'il possede : ");
		String typePouvoir = scanner.next();
		int puissancePouvoir = Util.checkSiEntre(Util.lireEntierAuClavier("Entrer la puissance du super pouvoir : "), 0, 10);
		if(puissancePouvoir == -1)
			return -1;
		int coordX = Util.checkSiEntre(Util.lireEntierAuClavier("Entrer la coordonn�e X o� vous l'avez aper�u : "), 0, 100);
		if(coordX == -1)
			return -1;
		int coordY = Util.checkSiEntre(Util.lireEntierAuClavier("Entrer � pr�sent la coordonn�e Y o� vous l'avez aper�u : "), 0, 100);
		if(coordY == -1)
			return -1;
		System.out.println("A quelle date l'avez vous aper�u : ");
		String date = scanner.next();
		System.out.println("Quel est son clan ? (M/D)");
		char clan = scanner.next().charAt(0);
		int victoires = Util.lireEntierAuClavier("Combien de victoires a t'il eu ? ");
		int defaites = Util.lireEntierAuClavier("Combien de d�faites a t'il eu ?");
		boolean estVivant;
		char vivantChar;
		do {
			System.out.println("Est t'il encore en vie ? (O/N)");
			vivantChar = scanner.next().charAt(0);
			estVivant = true;
			if(vivantChar == 'O' || vivantChar == 'o') {
				estVivant = true;
			} else if(vivantChar == 'N' || vivantChar == 'n') {
				estVivant = false;
			}
		} while (vivantChar != 'o' && vivantChar != 'O' && vivantChar != 'n' && vivantChar != 'N');
		int idSuperHero = - 1;
		try {
			idSuperHero = connexionDb.ajouterSuperHero(new SuperHero(nom, prenom, nomSuperHero, adresse, origine, typePouvoir,
					puissancePouvoir, coordX, coordY, date, clan, victoires, defaites, estVivant));
		} catch (ParseException e) {
			System.out.println("Erreur lors de l'encodage de la date");
		}
		if(idSuperHero < 0){
			System.out.println("L'ajout n'a pas pu �tre effectu�");
		} else {
			System.out.println("Le SuperHero est ajout� sous l'id : " + idSuperHero);
		}
		return idSuperHero;
	}

	public void rapportCombat() {
		ArrayList<Participation> participations = new ArrayList<Participation>();
		System.out.println("---------------------------------------------------");
		System.out.println("Bienvenue dans l'encodage d'un rapport de combat");
		System.out.println("---------------------------------------------------");
		System.out.println("Veuilliez tout d'abord indiquer la date du combat :(dd-mm-yyyy) ");
		String date = scanner.next();
		int coordX = Util.checkSiEntre(Util.lireEntierAuClavier("Quelle �tait la coordonn�e X du combat : "), 0, 100);
		if(coordX == -1)
			return;
		int coordY = Util.checkSiEntre(Util.lireEntierAuClavier("Quelle �tait la coordonn�e Y du combat : "), 0, 100);
		if(coordY == -1)
			return;
		int idCombat = -1;
		try {
			System.out.println("Nous allons � pr�sent passer � l'encodage des participations");
			int i = 0;
			char boucle;
			do {
				Participation participation = ajouterParticipation(idCombat, i);
				if(participation == null)
					return;
				participations.add(participation); 
				i++;
				System.out.println("Voulez vous ajouter une autre participation ? (O/N)");
				boucle = scanner.next().charAt(0);
			} while (Util.lireCharOouN(boucle));
			idCombat = connexionDb.ajouterCombat(new Combat(date, coordX, coordY, idAgent, 0, 0, 0, 0), participations);
		} catch (ParseException e) {
			System.out.println("Erreur lors de l'encodage de la date");
		}
		if(idCombat == -1){
			System.out.println("Erreur lors de l'ajout du combat");
		} else if (idCombat != -2) {
			System.out.println("Le combat a �t� ajout� sous l'id : " + idCombat);
		}
		
	}
	
	public Participation ajouterParticipation(int idCombat, int numeroLigne) {
		System.out.println("------------------------------------------------");
		System.out.println("Bienvenue dans l'encodage d'une participation");
		System.out.println("-------------------------------------------------");
		System.out.println("Commencer par entrer le surnom du superh�ros : ");
		String nomSuperHero = scanner.next();
		int idSuperHero = checkSiPresent(nomSuperHero);
		if(idSuperHero == -1){
			System.out.println("Ce h�ros n'est malheureusement pas connus de nos syst�mes, le processus d'inscription va commencer");
			idSuperHero = creationSuperHero(nomSuperHero);
		}
		if(idSuperHero == -1)
			return null;
		System.out.println("Comment s'est termine le combat pour cette personne (G/P/N) ? ");
		char issue = scanner.next().charAt(0); 
		return new Participation(idSuperHero, idCombat, issue, numeroLigne);
	}
	
	public void reperage(){
		System.out.println("-------------------------------------------");
		System.out.println("Bienvenue dans l'encodage d'un reperage");
		System.out.println("-------------------------------------------");
		System.out.println("Commencer par entrer le nom du superh�ros que vous avez aper�u : ");
		String nomSuperHero = scanner.next();
		int idSuperHero = checkSiPresent(nomSuperHero);
		if(idSuperHero == -1){
			System.out.println("Ce h�ros n'est malheureusement pas connus de nos syst�mes, le processus d'inscription va commencer");
			idSuperHero = creationSuperHero(nomSuperHero);
		}
		if(idSuperHero == -1)
			return;
		int coordX = Util.checkSiEntre(Util.lireEntierAuClavier("Veuilliez entrer la coordonn�e X o� vous avez aper�u le superh�ro: "), 0, 100);
		int coordY = Util.checkSiEntre(Util.lireEntierAuClavier("Veuilliez entrer la coordonn�e Y o� vous avez aper�u le superh�ro: "), 0, 100);
		System.out.println("A quelle date l'avez vous vu ? (dd-mm-yyyy)");
		String date = scanner.next();
		int idReperage = -1;
		try {
			idReperage = connexionDb.ajouterReperage(new Reperage(idAgent, idSuperHero, coordX, coordY, date));
		} catch (ParseException e) {
			System.out.println("Erreur lors de l'encodage de la date");
		}
		if(idReperage < 0) {
			System.out.println("Erreur lors de l'ajout du rep�rage");
		} else {
			System.out.println("Le rep�rage a bien �t� ajout�");
		}
	}

	public int checkSiPresent(String nomSuperHero) {
		SuperHero superhero = connexionDb.informationSuperHero(nomSuperHero);
		int idSuperHero = -1;
		if(superhero != null) {
			System.out.println("S'agit t'il de celui-ci ? (O/N)");
			char choix = scanner.next().charAt(0);
			if(Util.lireCharOouN(choix)){
				idSuperHero = superhero.getIdSuperhero();
			} else {
				idSuperHero = -2;
			}
			if(idSuperHero < 0){
				idSuperHero = creationSuperHero(null);
			}
		}
		return idSuperHero;
	}
	
	public void signalerDecesSH(){
		System.out.println("----------------------------------");
		System.out.println("Bienvenue en ce jour funeste");
		System.out.println("----------------------------------");
		System.out.println("Veuilliez entrer le nom du superh�ro : ");
		String nomSuperHero = scanner.next();
		int idSuperHero = checkSiPresent(nomSuperHero);
		if(idSuperHero < -1) {
			System.out.println("Le h�ros � miraculeusement surv�cus");
		} else if (idSuperHero < 0) {
			System.out.println("Aucun h�ro pr�sent sous ce nom l�");
		}else {
			System.out.println("Nous allons inhumer ce superh�ro ...");
			connexionDb.supprimerSuperHero(idSuperHero);
		}
	}
}

