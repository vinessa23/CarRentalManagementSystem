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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.PaymentStatus;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerEmailExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OutletNotFoundException;
import util.exception.OutletNotOpenYetException;
import util.exception.ReservationAlreadyCancelledException;
import util.exception.ReservationIdExistException;
import util.exception.ReservationNotFoundException;
import util.exception.ReturnBeforePickupDateException;
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
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public MainApp()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        currentCustomer = null;
    }

    public MainApp(ReservationSessionBeanRemote reservationSessionBeanRemote, ModelSessionBeanRemote modelSessionBean, CategorySessionBeanRemote categorySessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote, OutletSessionBeanRemote outletSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote) {
        this();
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
                        try {
                            doSearchCar();
                        } catch (ReturnBeforePickupDateException ex) {
                            System.out.println(ex.getMessage());
                        }
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

        Customer customer = new Customer(name, email, password);
        
        Set<ConstraintViolation<Customer>>constraintViolations = validator.validate(customer);

        if(constraintViolations.isEmpty())
        {
            try {
                Long id = customerSessionBeanRemote.createNewCustomer(customer);
                System.out.println("Sign up successful! Customer ID: " + id);
            } catch (CustomerEmailExistException | UnknownPersistenceException ex) {
                System.out.println("Email already exists!");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForCustomer(constraintViolations);
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
            System.out.print("\n*** Merlion Car Rental :: Main Menu ***\n");
            System.out.println("You are logged in as " + currentCustomer.getName());
            System.out.println("");
            System.out.println("1: Search Car");
            System.out.println("2: View Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try {
                        doSearchCar();
                    } catch (ReturnBeforePickupDateException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                else if (response == 2)
                {
                    doViewReservationDetails();
                }
                else if (response == 3)
                {
                    doViewAllMyReservations();
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }          
            if(response == 4)
            {
                currentCustomer = null;
                break;
            }
        }
    }
    
    private void doSearchCar() throws ReturnBeforePickupDateException {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("HH:mm");
            Date startDate;
            Date endDate;

            System.out.println("*** Merlion Car Rental :: Search Car ***\n");
            System.out.print("Enter pickup date (dd/mm/yyyy HH:mm)> ");
            startDate = inputDateFormat.parse(scanner.nextLine().trim());
            
            System.out.print("Enter return date (dd/mm/yyyy HH:mm)> ");
            endDate = inputDateFormat.parse(scanner.nextLine().trim());
            
            if(endDate.before(startDate)) {
                throw new ReturnBeforePickupDateException ("Return date must be after pickup date!");
            }
            
            List<Outlet> outlets = outletSessionBeanRemote.retrieveAllOutlets();
            System.out.println("\n****** Our Outlets ******");
            System.out.printf("%10s%20s%20s%20s%20s\n", "Seq No.", "Outlet Name", "Address", "Opening Hour", "Closing Hour");
            
            for(int i = 0; i < outlets.size(); i++)
            {
                Outlet o = outlets.get(i);
                System.out.printf("%10s%20s%20s%20s%20s\n", (i + 1), o.getName(), o.getAddress(), outputDateFormat.format(o.getOpeningHour()), outputDateFormat.format(o.getClosingHour()));
            }
            
            System.out.println("------------------------");
            System.out.print("Enter seq no. for pickup oulet> ");
            int pickupOutletNumber = scanner.nextInt();
            while(pickupOutletNumber <= 0 || pickupOutletNumber > outlets.size()) {
                System.out.print("Enter valid sequence number!");
                System.out.print("Enter seq no. for pickup oulet> ");
                pickupOutletNumber = scanner.nextInt();
            }
            Outlet pickupOutlet = outlets.get(pickupOutletNumber - 1);
            
            System.out.print("Enter seq no. for return oulet> ");
            int returnOutletNumber = scanner.nextInt();
            while(returnOutletNumber <= 0 || returnOutletNumber > outlets.size()) {
                System.out.print("Enter valid sequence number!\n");
                System.out.print("Enter seq no. for return oulet> ");
                returnOutletNumber = scanner.nextInt();
            }
            Outlet returnOutlet = outlets.get(returnOutletNumber - 1);
            
            
            //FOR DEBUGGING
            //List<Category> categories = categoriesAvailableForThisPeriod(pickupOutlet, startDate, endDate);
            
            
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
            System.out.println(ex.getMessage());
        }
    }

        
    private void doReserveCar(List<Packet> packets, Outlet pickupOutlet, Outlet returnOutlet, Date start, Date end) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                
        System.out.println("*** Merlion Car Rental :: Reserve Car ***");
        if(currentCustomer != null) {
            try {
                System.out.print("Enter seq no. for category that you want to reserve> ");
                int chosen = scanner.nextInt();
                while(chosen <= 0 || chosen > packets.size() || !packets.get(chosen - 1).isCanReserve()) {
                    System.out.print("Please enter a valid sequence number!\n");
                    System.out.print("Enter seq no. for category that you want to reserve> ");
                    chosen = scanner.nextInt();
                    
                }
                Packet chosenPacket = packets.get(chosen - 1);
                scanner.nextLine();
                //payment method
                System.out.print("Enter credit card number> ");
                String creditCardNumber = scanner.nextLine().trim();
                System.out.print("Enter name on credit card> ");
                String nameOnCard = scanner.nextLine().trim();
                System.out.print("Enter CVV> ");
                String cvv = scanner.nextLine().trim();
                System.out.print("Enter expiry date (dd/mm/yyyy)> ");
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
                //System.out.println(currentCustomer.getCustomerId());
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
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Please login first before making a reservation!\n");
        }
    }
    
    private void doViewReservationDetails() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        
        System.out.println("*** Merlion Car Rental :: View Reservation Details ***\n");

        try
        {
            List<Reservation> reservations = reservationSessionBeanRemote.retrieveMyReservations(currentCustomer.getCustomerId());
            //print out reservation id, booking status, start date, end date,  pickup outlet, return outlet, category, total amount, payment status
            if(reservations.isEmpty()) {
                System.out.println("You have no reservation!");
                return;
            }
            System.out.printf("%15s%20s%20s%20s%20s%20s%20s\n", "Seq No.", "Reservation ID", "Booking Status", "Pickup Date", "Return Date", "Pickup Outlet", "Return Outlet");
            int i = 1;
            for(Reservation reservation:reservations)
            {
                System.out.printf("%15s%20s%20s%20s%20s%20s%20s\n", i, reservation.getReservationId(), reservation.getBookingStatus(), outputDateFormat.format(reservation.getStartDate()), outputDateFormat.format(reservation.getEndDate()), reservation.getPickupOutlet().getName(), reservation.getReturnOutlet().getName());
                i++;
            }
            System.out.println("------------------------");
            System.out.print("Enter Seq No. for reservation that you want to view> ");
            int seqNo = scanner.nextInt();
            while(seqNo <= 0 || seqNo > reservations.size()) {
                    System.out.print("Please enter a valid sequence number!\n");
                    System.out.print("Enter seq no. for reservation that you want to view> ");
                    seqNo = scanner.nextInt();
                    
            }
            
            Reservation reservation = reservations.get(seqNo - 1);
            String cancellationTime;
            if(reservation.getCancellationTime() == null) {
                cancellationTime = "NA";
            } else {
                cancellationTime = outputDateFormat.format(reservation.getCancellationTime());
            }
            
            String pickupCustomer;
            if(reservation.getPickUpCustomerName() == null) {
                pickupCustomer = "NA";
            } else {
                pickupCustomer = reservation.getPickUpCustomerName() + " (email: " + reservation.getPickUpCustomerEmail() + ")";
            }
            
            String returnCustomer;
            if(reservation.getReturnCustomerName() == null) {
                returnCustomer = "NA";
            } else {
                returnCustomer = reservation.getReturnCustomerName() + " (email: " + reservation.getReturnCustomerEmail() + ")";
            }
            
            System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%30s%30s\n", "Reservation ID", "Booking Status", "Cancellation Date", "Pickup Date", "Return Date", "Pickup Outlet", "Return Outlet" ,"Category", "Total Amount", "Payment Status", "Pickup Customer Details", "Return Customer Details");
            
            System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%30s%30s\n", reservation.getReservationId(), reservation.getBookingStatus(), cancellationTime, outputDateFormat.format(reservation.getStartDate()), outputDateFormat.format(reservation.getEndDate()), reservation.getPickupOutlet().getName(), reservation.getReturnOutlet().getName(), reservation.getCategory().getCategoryName(), NumberFormat.getCurrencyInstance().format(reservation.getTotalAmount()), reservation.getPaymentStatus(), pickupCustomer, returnCustomer);
            System.out.println("------------------------");
            System.out.println("1: Cancel Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();

            if(response == 1)
            {
                doCancelReservation(reservation);
            }
        } catch (CustomerNotFoundException ex) {
            System.out.println("Customer ID not found");
        }
    }
    
    private void doViewAllMyReservations() {
        try {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            System.out.println("*** Merlion Car Rental :: View All My Reservations ***\n");
            
            List<Reservation> reservations = reservationSessionBeanRemote.retrieveMyReservations(currentCustomer.getCustomerId());
            if(reservations.isEmpty()) {
                System.out.println("You have no reservation!");
                return;
            }
            //print out reservation id, booking status, start date, end date,  pickup outlet, return outlet, category, total amount, payment status
            System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", "Reservation ID", "Booking Status", "Pickup Date", "Return Date", "Pickup Outlet", "Return Outlet" ,"Category", "Total Amount", "Payment Status");
            for(Reservation reservation:reservations)
            {
                System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", reservation.getReservationId(), reservation.getBookingStatus(), outputDateFormat.format(reservation.getStartDate()), outputDateFormat.format(reservation.getEndDate()), reservation.getPickupOutlet().getName(), reservation.getReturnOutlet().getName(), reservation.getCategory().getCategoryName(), NumberFormat.getCurrencyInstance().format(reservation.getTotalAmount()), reservation.getPaymentStatus());
            }
            
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (CustomerNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doCancelReservation(Reservation reservation) {
        Scanner scanner = new Scanner(System.in);     
        String input;
        
        System.out.println("*** Merlion Car Rental :: View Reservation Details :: Cancel Reservation ***\n");
        System.out.printf("Confirm cancel reservation with ID %s  (Enter 'Y' to confirm cancel)> ", reservation.getReservationId());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try 
            {
                LocalDateTime nowLdt = LocalDateTime.now();
                Date now = Date.from(nowLdt.atZone(ZoneId.systemDefault()).toInstant());
                String res = reservationSessionBeanRemote.cancelReservation(reservation.getReservationId(), now);
                System.out.println("Reservation cancelled successfully! " + res + "\n");
            } catch (ReservationAlreadyCancelledException | ReservationNotFoundException ex) {
                System.out.println(ex.getMessage());
            } 
        }
        else
        {
            System.out.println("Reservation NOT cancelled!\n");
        }
    }
    
    private void showInputDataValidationErrorsForCustomer(Set<ConstraintViolation<Customer>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
}
