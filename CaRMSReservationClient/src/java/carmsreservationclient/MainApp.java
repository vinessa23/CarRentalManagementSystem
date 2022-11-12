/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsreservationclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Customer;
import entity.Outlet;
import entity.Reservation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.PaymentStatus;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerEmailExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OutletNotFoundException;
import util.exception.OutletNotOpenYetException;
import util.exception.ReservationIdExistException;
import util.exception.UnknownPersistenceException;
import util.helperClass.Packet;

/**
 *
 * @author vinessa
 */
public class MainApp {

    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private ModelSessionBeanRemote modelSessionBean;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private CarSessionBeanRemote carSessionBeanRemote;
    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    private OutletSessionBeanRemote outletSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;

    private Customer currentCustomer;
    
    public MainApp() {
        currentCustomer = null;
    }

    public MainApp(ReservationSessionBeanRemote reservationSessionBeanRemote, ModelSessionBeanRemote modelSessionBean, CategorySessionBeanRemote categorySessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote, OutletSessionBeanRemote outletSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote) {
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.modelSessionBean = modelSessionBean;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.outletSessionBeanRemote = outletSessionBeanRemote;
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
                System.out.println("3: Search Car");
                System.out.println("4: Exit\n");
                response = 0;

                while (response < 1 || response > 4) {
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
                        doSearchCar();
                    } else if (response == 4) {
                        break;
                    } else {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }

                if (response == 4) {
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
            System.out.print("*** Merlion Car Rental :: Main Menu ***\n");
            System.out.println("You are logged in as " + currentCustomer.getName());
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
                    //doReserveCar();
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
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("HH:mm");
            Date startDate;
            Date endDate;

            System.out.println("*** Merlion Car Rental :: Search Car ***\n");
            System.out.print("Enter Pickup Date (dd/mm/yyyy HH:mm)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            
            //for testing
            //System.out.println(inputDateFormat.format(startDate));
            
            System.out.print("Enter Return Date (dd/mm/yyyy HH:mm)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());
            
            //for testing
            //System.out.println(inputDateFormat.format(endDate));
            
            List<Outlet> outlets = outletSessionBeanRemote.retrieveAllOutlets();
            System.out.println("\n****** Our Outlet ******");
            System.out.printf("%10s%30s%30s%30s%30s\n", "Seq No.", "Outlet Name", "Address", "Opening Hour", "Closing Hour");
            
            for(int i = 0; i < outlets.size(); i++)
            {
                Outlet o = outlets.get(i);
                System.out.printf("%10s%30s%30s%30s%30s\n", (i + 1), o.getName(), o.getAddress(), outputDateFormat.format(o.getOpeningHour()), outputDateFormat.format(o.getClosingHour()));
            }
            
            System.out.println("------------------------");
            System.out.print("Enter Seq No. for Pickup Oulet> ");
            int pickupOutletNumber = scanner.nextInt();
            while(pickupOutletNumber < 0 || pickupOutletNumber >= outlets.size()) {
                System.out.print("Enter valid sequence number!");
                System.out.print("Enter Seq No. for Pickup Oulet> ");
                pickupOutletNumber = scanner.nextInt();
            }
            Outlet pickupOutlet = outlets.get(pickupOutletNumber - 1);
            
            System.out.print("Enter Seq No. for Return Oulet> ");
            int returnOutletNumber = scanner.nextInt();
            while(returnOutletNumber < 0 || returnOutletNumber >= outlets.size()) {
                System.out.print("Enter valid sequence number!\n");
                System.out.print("Enter Seq No. for Return Oulet> ");
                returnOutletNumber = scanner.nextInt();
            }
            Outlet returnOutlet = outlets.get(returnOutletNumber - 1);
            
            List<Packet> packets = reservationSessionBeanRemote.searchCar(startDate, endDate, pickupOutlet, returnOutlet);
            System.out.println("\n****** Your search result ******");
            System.out.printf("%10s%20s%40s\n", "Seq No.", "Category Name", "Total Amount");
            for(int i = 1; i <= packets.size(); i++) {
                Packet p = packets.get(i - 1);
                System.out.printf("%10s%s\n", i, p.toString());
            }
            System.out.println("------------------------"); 
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            if(response == 1)
            {
                doReserveCar(packets, pickupOutlet, returnOutlet, startDate, endDate);
            }
            
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (OutletNotOpenYetException ex) {
            System.out.println("Outlet is not open yet!");
        }
    }

        
    private void doReserveCar(List<Packet> packets, Outlet pickupOutlet, Outlet returnOutlet, Date start, Date end) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                
        System.out.println("*** Merlion Car Rental :: Reserve Car ***");
        if(currentCustomer != null) {
            try {
                System.out.print("Enter Seq No. for category that you want to reserve> ");
                int chosen = scanner.nextInt();
                while(chosen < 0 || chosen >= packets.size() || !packets.get(chosen - 1).isCanReserve()) {
                    System.out.print("Please enter a valid sequence number!\n");
                    System.out.print("Enter Seq No. for category that you want to reserve> ");
                    chosen = scanner.nextInt();
                    
                }
                Packet chosenPacket = packets.get(chosen - 1);
                scanner.nextLine();
                //payment method
                System.out.print("Enter Credit Card Number> ");
                String creditCardNumber = scanner.nextLine().trim();
                System.out.print("Enter Name on Credit Card> ");
                String nameOnCard = scanner.nextLine().trim();
                System.out.print("Enter CVV> ");
                String cvv = scanner.nextLine().trim();
                System.out.print("Enter Expiry Date (dd/mm/yyyy)> ");
                Date expiry = inputDateFormat.parse(scanner.nextLine().trim());
                
                System.out.print("Pay now or pay during pickup? (Enter 'N' to pay now)> ");
                String whenPay = scanner.nextLine().trim();
                PaymentStatus status;
                if(whenPay.equals("N")) {
                    status = PaymentStatus.UPFRONT;
                } else {
                    status = PaymentStatus.PICKUP;
                }

                Reservation reservation = new Reservation(status, start, end, creditCardNumber, nameOnCard, cvv, expiry);
                //for testing
                System.out.println(currentCustomer.getCustomerId());
                Long reservationId = reservationSessionBeanRemote.reserveCar(currentCustomer.getCustomerId(), chosenPacket, pickupOutlet.getOutletId(), returnOutlet.getOutletId(), reservation);
                if(status == PaymentStatus.UPFRONT) {
                    String paymentId = reservationSessionBeanRemote.chargeAmountToCC(chosenPacket.getAmount(), creditCardNumber, nameOnCard, cvv, expiry);
                    System.out.println("Payment successful! Payment ID: " + paymentId);
                }
                System.out.println("Reservation successful! Reservation ID: " + reservationId);
            } catch (ParseException ex) {
                System.out.println("Invalid date input!\n");
            } catch (ReservationIdExistException | CustomerNotFoundException | CarNotFoundException | CategoryNotFoundException | OutletNotFoundException | UnknownPersistenceException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Please login first before making a reservation!\n");
        }
    }
    
    private void doCancelReservation() {
        
    }
    
    private void doViewReservationDetails() {
        
    }
    
    private void doViewAllMyReservations() {
        
    }
}
