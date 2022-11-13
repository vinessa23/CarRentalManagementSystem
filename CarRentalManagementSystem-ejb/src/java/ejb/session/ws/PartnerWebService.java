/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Category;
import entity.Customer;
import entity.Outlet;
import entity.RentalRate;
import entity.Reservation;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @EJB
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    
    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    @WebMethod(operationName = "customerLogin")
    public Customer customerLogin(@WebParam(name = "email") String email, @WebParam(name = "password") String password) throws InvalidLoginCredentialException, CustomerNotFoundException {
        return customerSessionBeanLocal.customerLogin(email, password);
    }
    
    @WebMethod(operationName = "retrieveMyReservations")
    public List<Reservation> retrieveMyReservations(@WebParam(name = "customerId") Long customerId) throws CustomerNotFoundException {
        return reservationSessionBeanLocal.retrieveMyReservations(customerId);
    }
    
    
    @WebMethod(operationName = "cancelReservation")
    public String cancelReservation(@WebParam(name = "reservationId") Long reservationId, @WebParam(name = "cancellationDate") Date cancellationDate) throws ReservationNotFoundException, ReservationAlreadyCancelledException {
        return reservationSessionBeanLocal.cancelReservation(reservationId, cancellationDate);
    }
    
    @WebMethod(operationName = "searchCar")
    public List<Packet> searchCar(@WebParam(name = "start") String start, @WebParam(name = "end") String end, @WebParam(name = "pickupOutlet") Long pickupOutletId, @WebParam(name = "returnOutlet") Long returnOutletId) throws OutletNotOpenYetException, ParseException, OutletNotFoundException, ReturnBeforePickupDateException {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date startDate = inputDateFormat.parse(start);
            Date endDate = inputDateFormat.parse(end);
            if(endDate.before(startDate)) {
                throw new ReturnBeforePickupDateException("Return date must be after pickup date!");
            }
            Outlet pickupOutlet = outletSessionBeanLocal.retrieveOutletById(pickupOutletId);
            Outlet returnOutlet = outletSessionBeanLocal.retrieveOutletById(returnOutletId);
            return reservationSessionBeanLocal.searchCar(startDate, endDate, pickupOutlet, returnOutlet);
        } catch (ParseException ex) {
            throw ex;
        }
    }
    
    @WebMethod(operationName = "reserveCar")
    public Long reserveCar(@WebParam(name = "customerId") Long customerId, @WebParam(name = "packet") Packet packet, @WebParam(name = "pickupOutletId") Long pickupOutletId, @WebParam(name = "returnOutletId") Long returnOutletId, @WebParam(name = "reservation") Reservation reservation) throws ReservationIdExistException, CustomerNotFoundException, CarNotFoundException, CategoryNotFoundException, OutletNotFoundException, UnknownPersistenceException, InputDataValidationException {

        return reservationSessionBeanLocal.reserveCar(customerId, packet, pickupOutletId, returnOutletId, reservation);
    }
    
    @WebMethod(operationName = "retrieveAllOutlets")
    public List<Outlet> retrieveAllOutlets() {
        return outletSessionBeanLocal.retrieveAllOutlets();
    }
    
    @WebMethod(operationName = "chargeAmountToCC")
    public String chargeAmountToCC(BigDecimal amount, String ccNum, String nameOnCard, String cvv, Date expiryDate) {
        return reservationSessionBeanLocal.chargeAmountToCC(amount, ccNum, nameOnCard, cvv, expiryDate);
    }
    
    @WebMethod(operationName = "createNewCustomer")
    public Long createNewCustomer(Customer customer) throws CustomerEmailExistException, UnknownPersistenceException, InputDataValidationException{
        return customerSessionBeanLocal.createNewCustomer(customer);
    }
} 
    
//    @WebMethod(operationName = "customerLogin")
//    public Customer customerLogin(@WebParam(name = "email") String email, @WebParam(name = "password") String password) throws InvalidLoginCredentialException, CustomerNotFoundException {
//        try {
//            Customer customer = customerSessionBeanLocal.customerLogin(email, password);
//            
//            System.out.println("********** PartnerWebService.customerLogin(): Customer "
//                    + customer.getName()
//                    + " login remotely via web service");
//            
//            List<Reservation> reservations = reservationSessionBeanLocal.retrieveMyReservations(customer.getCustomerId());
//            for(Reservation r : reservations) {
//                em.detach(r);
//                em.detach(r.getBookingCustomer());
//                r.setBookingCustomer(null);
//            }
//            return customer;
//        } catch (InvalidLoginCredentialException ex) {
//            throw new InvalidLoginCredentialException(ex.getMessage());
//        } catch (CustomerNotFoundException ex) {
//            throw new CustomerNotFoundException(ex.getMessage());
//        }
//    }
//
//    @WebMethod(operationName = "retrieveMyReservations")
//    public List<Reservation> retrieveMyReservations(@WebParam(name = "customerId") Long customerId) throws CustomerNotFoundException {
//        try {
//            List<Reservation> reservations = reservationSessionBeanLocal.retrieveMyReservations(customerId);
//            for(Reservation r : reservations) {
//                em.detach(r);
//                em.detach(r.getBookingCustomer());
//                r.setBookingCustomer(null);
//                em.detach(r.getCategory());
//                r.getCategory().getReservations().remove(r);
//                List<RentalRate> rentals = r.getRentalRates();
//                for(RentalRate rr : rentals) {
//                    em.detach(rr);
//                    rr.getReservations().remove(r);
//                }
//            }
//            return reservations;
//        } catch (CustomerNotFoundException ex) {
//            throw new CustomerNotFoundException(ex.getMessage());
//        }
//    }
//    //cancelReservation(Long reservationId, Date cancellationDate)
//    @WebMethod(operationName = "cancelReservation")
//    public String cancelReservation(@WebParam(name = "reservationId") Long reservationId, @WebParam(name = "cancellationDate") Date cancellationDate) throws ReservationNotFoundException, ReservationAlreadyCancelledException {
//        try {
//            return reservationSessionBeanLocal.cancelReservation(reservationId, cancellationDate);
//        } catch (ReservationAlreadyCancelledException ex) {
//            throw new ReservationAlreadyCancelledException(ex.getMessage());
//        } catch (ReservationNotFoundException ex) {
//            throw new ReservationNotFoundException(ex.getMessage());
//        }
//    }
//    
//    @WebMethod(operationName = "searchCar")
//    public List<Packet> searchCar(Date start, Date end, Outlet pickupOutlet, Outlet returnOutlet) throws OutletNotOpenYetException {
//        List<Packet> packets = reservationSessionBeanLocal.searchCar(start, end, pickupOutlet, returnOutlet);
//        for(Packet p : packets) {
//            Category c = p.getCategory();
//            em.detach(c);
//            c.setModels(null);
//            c.setRentalRates(null);
//            c.setReservations(null);
//            List<RentalRate> rentals = p.getRentalRates();
//            for(RentalRate rr : rentals) {
//                em.detach(rr);
//                rr.setReservations(null);
//            }
//        }
//            //NOT DONE I THINK NEED TO DETACH MORE
//        return packets;
//    }
        
