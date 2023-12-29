package ui;

import java.util.Scanner;
import api.Api;
import java.io.IOException;

class UI {
	public static void main(String[] args) {
		Logo.getLogo();
		Api api = new Api();
		Scanner sc = new Scanner(System.in);
		int input;
		
		while (true) {
			try {
				System.out.println("""
						1 - zadnja ocena
						2 - ocenjevanja
						3 - današnji urnik
						4 - redovalnica
						5 - izostanki
						6 - prijava 
						0 - izhod""");
				System.out.println("==========================");
				System.out.print("Choose your destiny: ");
				input = sc.nextInt();
				System.out.println("==========================\n");

				switch (input) {
					case 1: 
						System.out.println(api.getGrade());
						break;
					case 2:
						System.out.println(api.getExams());
						break;
					case 3:
						System.out.println(api.getTodayTimetable());
						break;
					case 4:
						System.out.println(api.getGrades());
						break;
						//System.out.println(api.getThisWeekTimetable());
					case 5:
						System.out.println(api.getAbsences());
						break;
					case 6:
						System.out.println("1 - prijava\n2 - odjava");
						int choice = sc.nextInt();
						sc.nextLine();
						if (choice==1){
							System.out.println("Uporabniško ime:");
							String username = sc.nextLine();

							System.out.println("Geslo:");
							String password = sc.nextLine();

							System.out.println(api.login(username,password));
						} else if(choice==2){
							api.logout();
						} else if (choice==0){
							break;
						}
						else{
							System.out.println();
							throw new IllegalArgumentException("Vnesi številko od 0 do 2.");
						}
						break;
					case 0:
						sc.close();
						System.exit(0);
						break;
					default:
						throw new IllegalArgumentException("Vnesi številko od 0 do 6.");
				}
				System.out.println("\n==========================");
			}
			catch(IOException e) {
				System.out.println("NAPAKA: "+e.getMessage()+"\n");
			}
			catch(java.util.InputMismatchException e) {
				System.out.println("\nNAPAKA: Neveljaven vnos. Le vnos tipa int je veljaven.\n");
				sc.nextLine();
			}
			catch(IllegalArgumentException e){
			System.out.println("NAPAKA: "+e.getMessage()+"\n");}
		}
	}
}
