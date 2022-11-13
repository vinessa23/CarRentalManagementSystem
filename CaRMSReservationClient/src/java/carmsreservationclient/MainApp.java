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
import entity.Car;
import entity.Category;
import entity.Customer;
import entity.Outlet;
import entity.Reservation;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.BookingStatus;
import util.enumeration.CarStatusEnum;
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
//            System.out.println("2: Reserve Car");
//            System.out.println("3: Cancel Reservation");
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
                    doSearchCar();
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
            while(pickupOutletNumber <= 0 || pickupOutletNumber > outlets.size()) {
                System.out.print("Enter valid sequence number!");
                System.out.print("Enter Seq No. for Pickup Oulet> ");
                pickupOutletNumber = scanner.nextInt();
            }
            Outlet pickupOutlet = outlets.get(pickupOutletNumber - 1);
            
            System.out.print("Enter Seq No. for Return Oulet> ");
            int returnOutletNumber = scanner.nextInt();
            while(returnOutletNumber <= 0 || returnOutletNumber > outlets.size()) {
                System.out.print("Enter valid sequence number!\n");
                System.out.print("Enter Seq No. for Return Oulet> ");
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
                System.out.print("Enter Seq No. for category that you want to reserve> ");
                int chosen = scanner.nextInt();
                while(chosen <= 0 || chosen > packets.size() || !packets.get(chosen - 1).isCanReserve()) {
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
                    System.out.print("Enter Seq No. for reservation that you want to view> ");
                    seqNo = scanner.nextInt();
                    
            }
            Reservation reservation = reservations.get(seqNo - 1);
            System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", "Reservation ID", "Booking Status", "Pickup Date", "Return Date", "Pickup Outlet", "Return Outlet" ,"Category", "Total Amount", "Payment Status");
            System.out.printf("%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", reservation.getReservationId(), reservation.getBookingStatus(), outputDateFormat.format(reservation.getStartDate()), outputDateFormat.format(reservation.getEndDate()), reservation.getPickupOutlet().getName(), reservation.getReturnOutlet().getName(), reservation.getCategory().getCategoryName(), NumberFormat.getCurrencyInstance().format(reservation.getTotalAmount()), reservation.getPaymentStatus());
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
        System.out.printf("Confirm Delete Reservation with ID %s  (Enter 'Y' to Cancel)> ", reservation.getReservationId());
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

//    //for debugging
//    
//       //step 1: what are the categories that are available for the outlet for the period
//    //how do return outlet affect?
//    public List<Category> categoriesAvailableForThisPeriod(Outlet outlet, Date start, Date end) {
//        List<Category> all = categorySessionBeanRemote.retrieveAllCategories();
//        List<Category> res = new ArrayList<>();
//        for(Category c : all) {
//            System.out.println(c.getCategoryName());
//            if(isCategoryAvailableForThisPeriod(outlet, c, start, end)) {
//                res.add(c);
//            }
//        }
//        return res;
//    }
//    
//    //step 2: is that particular category available for that period
//    //???? do we need to care about the return outlet?
//    private boolean isCategoryAvailableForThisPeriod(Outlet outlet, Category category, Date start, Date end) {
//        //find out whether the category is available for that pickup outlet
//        System.out.println("checking same outlet");
//        if(isCategoryAvailableForThisPeriodOutlet(outlet, category, start, end, true)) {
//            return true;
//        } else {
//            //if not, then find out whether that category is available for the other outlets
//            System.out.println("checking other outlet");
//            List<Outlet> outlets = outletSessionBeanRemote.retrieveAllOutlets();
//            for(Outlet o : outlets) {
//                if(!o.equals(outlet)) {
//                    if(isCategoryAvailableForThisPeriodOutlet(o, category, start, end, false)) {
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//    }
//    
//    //step 3: is that category available for that period (with time adjustment for outlet other than pickup outlet)
//    private boolean isCategoryAvailableForThisPeriodOutlet(Outlet outlet, Category category, Date start, Date end, boolean sameOutlet) {
//        //car available in the pickup outlet
//        int carAvailable = numCarsForCategoryAndOutlet(category, outlet);
//        System.out.println("car available: " + carAvailable);
//        int carReserved = numOverlappingReservations(outlet, category, start, end, sameOutlet);
//        System.out.println("overlapping: " + carReserved);
//        if (carAvailable > carReserved) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//    
//    
//    //counting the number of car is the outlet for that category
//    //??? but how do u know that the car will still be in that outlet since the car can move after reservation (and car only hv current outlet)
//    private int numCarsForCategoryAndOutlet(Category category, Outlet outlet) {
//        List<Car> cars = outlet.getCars();
//        int result = 0;
//        for(Car car : cars) {
//            System.out.println(car.getLicensePlate() + " category: " + car.getModel().getCategory().getCategoryId());
//            System.out.println("target category is " + category.getCategoryId());
//            if(car.getModel().getCategory().getCategoryId().equals(category.getCategoryId())) {
//                if(car.getEnabled() == true && car.getCarStatus() == CarStatusEnum.AVAILABLE) {
//                    result++;
//                }
//            }
//        }
//        return result;
//    }
//
//    //count the number of overlapping reservations
//    private int numOverlappingReservations(Outlet outlet, Category category, Date start, Date end, boolean sameOutlet) {
//        try {
//            List<Reservation> reservationThisOutlet = retrieveActiveReservationsOutlet(outlet);
//            List<Reservation> reservationCategory = new ArrayList<>();
//            for(Reservation r : reservationThisOutlet) {
//                if(r.getCategory().getCategoryId().equals(category.getCategoryId())) {
//                    reservationCategory.add(r);
//                }
//            }
//            int res = 0;
//            for(Reservation r : reservationCategory) {
//                //???? shld check whether the pickup location selected = return location of reservation
//                // and return location selected = pickup location of reservation
//                if(sameOutlet) {
//                    if(start.before(r.getEndDate()) || end.after(r.getStartDate())) {
//                        res++;
//                    }
//                } else {
//                    //original idea: add 2 hours to the end date of reservations and minus 2 hours for the starting date of reservations if outlet different 
//                    //???? shldnt this be add 2 hours for the pickup outlet diff than the return outlet of those reservations 
//                    //minus 2 hours if the return outlet is diff from the pickup outlet of those reservations
//                    
//                    //dont need the end after
//                    //less than 2 hrs need to count
//                    if(start.before(plusHours(r.getEndDate(), 2)) || end.after(plusHours(r.getStartDate(), -2))) {
//                        res++;
//                    }
//                }
//            }
//            return res;
//        } catch (ReservationNotFoundException ex) { //if no reservation then no overlapping reservations
//            return 0;
//        }  
//    }
//    
//    private List<Reservation> retrieveActiveReservationsOutlet(Outlet outlet) throws ReservationNotFoundException{
//        try {
//            List<Reservation> all = reservationSessionBeanRemote.retrieveAllReservations();
//            List<Reservation> res = new ArrayList<>();
//            for(Reservation r : all) {
//                if(r.getPickupOutlet().getOutletId().equals(outlet.getOutletId())){
//                    if(r.getBookingStatus() == BookingStatus.ACTIVE) {
//                        res.add(r);
//                    }
//                }
//            }
//            return res;
//        } catch (ReservationNotFoundException ex) {
//            throw new ReservationNotFoundException("No reservations");
//        }
//    }
//    
//
//    
//    private Date plusHours(Date date, int hour) {
//        LocalDateTime initialLDT = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        LocalDateTime afterLDT = initialLDT.plusHours(hour);
//        return java.sql.Timestamp.valueOf(afterLDT);
//    }
    
}
