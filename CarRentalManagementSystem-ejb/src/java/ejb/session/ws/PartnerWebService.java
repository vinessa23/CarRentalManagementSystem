/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Category;
import entity.Customer;
import entity.Outlet;
import entity.RentalRate;
import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
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
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

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
    public List<Packet> searchCar(@WebParam(name = "start") Date start, @WebParam(name = "end") Date end, @WebParam(name = "pickupOutlet") Outlet pickupOutlet, @WebParam(name = "returnOutlet") Outlet returnOutlet) throws OutletNotOpenYetException {
        return reservationSessionBeanLocal.searchCar(start, end, pickupOutlet, returnOutlet);
    }
    
    @WebMethod(operationName = "reserveCar")
    public Long reserveCar(@WebParam(name = "customerId") Long customerId, @WebParam(name = "packet") Packet packet, @WebParam(name = "pickupOutletId") Long pickupOutletId, @WebParam(name = "returnOutletId") Long returnOutletId, @WebParam(name = "reservation") Reservation reservation) throws ReservationIdExistException, CustomerNotFoundException, CarNotFoundException, CategoryNotFoundException, OutletNotFoundException, UnknownPersistenceException {
        return reservationSessionBeanLocal.reserveCar(customerId, packet, pickupOutletId, returnOutletId, reservation);
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
        
