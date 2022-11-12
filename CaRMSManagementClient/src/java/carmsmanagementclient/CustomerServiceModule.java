/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsmanagementclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Customer;
import entity.Employee;
import entity.Reservation;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.EmployeeRoles;
import util.enumeration.PaymentStatus;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidEmployeeRoleException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author YC
 */
public class CustomerServiceModule {
    
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private Employee currentEmployee;

    public CustomerServiceModule() {
    }

    public CustomerServiceModule(ReservationSessionBeanRemote reservationSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote, Employee currentEmployee) {
        this();
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.currentEmployee = currentEmployee;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
    }
    
    public void menuCustomerService() throws InvalidEmployeeRoleException {
        
        if(currentEmployee.getRole()!= EmployeeRoles.CUSTOMERSERVICE)
        {
            throw new InvalidEmployeeRoleException("You don't have CUSTOMER SERVICE rights to access the customer service module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion Car Rental Management :: Customer Service ***\n");
            System.out.println("1: Pickup Car");
            System.out.println("2: Return Car");
            System.out.println("3: Back\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doPickupCar();
                }
                else if (response == 2)
                {
                    doReturnCar();
                }
                else if (response == 3) {
                    break;
                }
                else 
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if(response == 3)
            {
                break;
            }
        } 
    }
    
    private void doPickupCar() {
        try {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            System.out.println("*** Merlion Car Rental Management :: Customer Service :: Pickup Car ***\n");
            System.out.print("Enter Booking Customer Email> ");
            String email = scanner.nextLine().trim();
            
            Customer customer = customerSessionBeanRemote.retrieveCustomerByEmail(email);
            List<Reservation> reservations = reservationSessionBeanRemote.retrieveMyActiveReservations(customer.getCustomerId());
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
            System.out.print("Enter Seq No. for reservation that the customer picks up> ");
            int seqNo = scanner.nextInt();
            while(seqNo <= 0 || seqNo > reservations.size()) {
                System.out.print("Please enter a valid sequence number!\n");
                System.out.print("Enter Seq No. for reservation that the customer picks up> ");
                seqNo = scanner.nextInt();
                
            }
            Reservation reservation = reservations.get(seqNo - 1);
            
            if(reservation.getPaymentStatus() == PaymentStatus.PICKUP) {
                String paymentId = reservationSessionBeanRemote.chargeAmountToCC(reservation.getTotalAmount(), reservation.getCcNum(), reservation.getNameOnCard(), reservation.getCvv(), reservation.getExpiryDate());
                System.out.println("Payment successful! Payment ID: " + paymentId);
            }
            
            System.out.print("Enter customer name who picks up> ");
            String pickupName = scanner.nextLine().trim();
            System.out.print("Enter customer email who picks up> ");
            String pickupEmail = scanner.nextLine().trim();
            
            reservationSessionBeanRemote.pickupCar(reservation.getReservationId(), pickupName, pickupEmail);
        } catch (CustomerNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (ReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
    private void doReturnCar() {
        try {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            System.out.println("*** Merlion Car Rental Management :: Customer Service :: Return Car ***\n");
            System.out.print("Enter Booking Customer Email> ");
            String email = scanner.nextLine().trim();
            
            Customer customer = customerSessionBeanRemote.retrieveCustomerByEmail(email);
            List<Reservation> reservations = reservationSessionBeanRemote.retrieveMyActiveReservations(customer.getCustomerId());
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
            System.out.print("Enter Seq No. for reservation that the customer picks up> ");
            int seqNo = scanner.nextInt();
            while(seqNo <= 0 || seqNo > reservations.size()) {
                System.out.print("Please enter a valid sequence number!\n");
                System.out.print("Enter Seq No. for reservation that the customer picks up> ");
                seqNo = scanner.nextInt();
                
            }
            Reservation reservation = reservations.get(seqNo - 1);
            
            System.out.print("Enter customer name who returns the car> ");
            String returnName = scanner.nextLine().trim();
            System.out.print("Enter customer email who returns the car> ");
            String returnEmail = scanner.nextLine().trim();
            
            reservationSessionBeanRemote.returnCar(reservation.getReservationId(), returnName, returnEmail);
        } catch (CustomerNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (ReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
