/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsreservationclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import entity.Customer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.CustomerEmailExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
public class MainApp {

    private CustomerSessionBeanRemote customerSessionBeanRemote;

    private Customer currentCustomer;
    
    private CarSessionBeanRemote carSessionBeanRemote;
    
    public MainApp() {
        currentCustomer = null;
    }

    //initialise the remote SB attributes
    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote) {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Merlion Car Rental ***\n");

            if (currentCustomer == null) {
                System.out.println("1: Sign Up");
                System.out.println("2: Login");
                System.out.println("3: Exit\n");
                response = 0;

                while (response < 1 || response > 3) {
                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {
                        doSignUp();

                    } else if (response == 2) {
                        try {
                            doLogin();
                            System.out.println("Login successful!\n");
                            menuMain();
                        } catch (InvalidLoginCredentialException ex) {
                            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                        }
                    } else if (response == 3) {
                        break;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

                if (response == 3) {
                    break;
                }

            } else {
                System.out.println("You are login as " + currentCustomer.getName() + "\n");
                menuMain();
            }
        }
    }

    private void doSignUp() {
        try {
            Scanner scanner = new Scanner(System.in);
            String name = "";
            String email = "";
            String password = "";
            
            System.out.println("*** Merlion Car Rental :: Sign Up ***\n");
            System.out.print("Enter name> ");
            name = scanner.nextLine().trim();
            System.out.print("Enter email> ");
            email = scanner.nextLine().trim();
            System.out.print("Enter password> ");
            password = scanner.nextLine().trim();
            
            currentCustomer = new Customer(name, email, password);
            customerSessionBeanRemote.createNewCustomer(currentCustomer);
        } catch (CustomerEmailExistException | UnknownPersistenceException ex) {
            System.out.println("Email already exists!");
        }
    }
       

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";

        System.out.println("*** Merlion Car Rental :: Login ***\n");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (email.length() > 0 && password.length() > 0) {
            currentCustomer = customerSessionBeanRemote.customerLogin(email, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }

    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion Car Rental :: Main Menu ***\n");
            System.out.println("1: Search Car");
            System.out.println("2: Reserve Car");
            System.out.println("3: Cancel Reservation");
            System.out.println("4: View Reservation Details");
            System.out.println("5: View All My Reservations");
            System.out.println("6: Logout\n");
            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doSearchCar();
                }
                else if (response == 2)
                {
                    doReserveCar();
                }
                else if (response == 3)
                {
                    
                }
                else if (response == 4)
                {
                    break;
                }
                else if (response == 5)
                {
                    break;
                }
                else if (response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }          
            if(response == 6)
            {
                currentCustomer = null;
                runApp();
            }
        }
    }
    
    private void doSearchCar() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date departureDate;
            Date returnDate;

            System.out.println("*** Merlion Car Rental :: Search Car ***\n");
            System.out.print("Enter Departure Date (dd/mm/yyyy)> ");
            departureDate = inputDateFormat.parse(scanner.nextLine().trim());
            
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
    }
    
    private void doReserveCar() {
        //rmb to get the payment status as well (whether paying upfront or deferred
    }
    
    private void doCancelReservation() {
        
    }
    
    private void doViewReservationDetails() {
        
    }
    
    private void doViewAllMyReservations() {
        
    }
}
