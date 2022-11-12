/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.Outlet;
import entity.RentalRate;
import entity.Reservation;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Local;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
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
@Local
public interface ReservationSessionBeanLocal {

    public List<Packet> searchCar(Date start, Date end, Outlet pickupOutlet, Outlet returnOutlet) throws OutletNotOpenYetException;

    public List<Reservation> retrieveAllReservations() throws ReservationNotFoundException;

    public Reservation getReservation(Long id) throws ReservationNotFoundException;

    public String cancelReservation(Long reservationId, Date cancellationDate) throws ReservationAlreadyCancelledException, ReservationNotFoundException;

    public Long reserveCar(Long customerId, Packet packet, Long pickupOutletId, Long returnOutletId, Reservation reservation) throws ReservationIdExistException, CustomerNotFoundException, CarNotFoundException, CategoryNotFoundException, OutletNotFoundException, UnknownPersistenceException;
    
    public void pickupCar(Long reservationId, String pickupCustomerName, String pickupCustomerEmail) throws ReservationNotFoundException;
    
    public void returnCar(Long reservationId, String returnCustomerName, String returnCustomerEmail) throws ReservationNotFoundException;

    public List<Reservation> retrieveReservationsOnDate(Date date) throws ReservationNotFoundException;
    
}
