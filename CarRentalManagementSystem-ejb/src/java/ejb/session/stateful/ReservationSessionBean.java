/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import entity.Car;
import entity.Category;
import entity.Customer;
import entity.Outlet;
import entity.Reservation;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.ReservationIdExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Stateful
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB
    private CategorySessionBeanLocal categorySessionBeanLocal;

    @EJB
    private CarSessionBeanLocal carSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;
    
    private Long reserveCar(Long customerId, Long carId, Long categoryId, Long pickupOutletId, Long returnOutletId, Reservation reservation) throws ReservationIdExistException, CustomerNotFoundException, CarNotFoundException, CategoryNotFoundException, OutletNotFoundException, UnknownPersistenceException {
        try {
            Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
            Car car = carSessionBeanLocal.retrieveCarById(carId);
            Category category = categorySessionBeanLocal.retrieveCategoryById(categoryId);
            Outlet pickupOutlet = outletSessionBeanLocal.retrieveOutletById(pickupOutletId);
            Outlet returnOutlet = outletSessionBeanLocal.retrieveOutletById(returnOutletId);
            
            customer.getReservations().add(reservation);
            reservation.setBookingCustomer(customer);
            reservation.setCar(car);
            car.setCurrentCustomer(customer);
            reservation.setPickupOutlet(pickupOutlet);
            reservation.setReturnOutlet(returnOutlet);
            //how to add rental rate :(
            reservation.setCategory(category);
            category.getReservations().add(reservation);
            em.persist(reservation);
            em.flush();
            
            return reservation.getReservationId();
        }catch (PersistenceException ex){
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new ReservationIdExistException();
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            } 
        } catch (CustomerNotFoundException ex) {
            throw new CustomerNotFoundException(ex.getMessage());
        } catch (CarNotFoundException ex) {
            throw new CarNotFoundException(ex.getMessage());
        } catch (CategoryNotFoundException ex) {
            throw new CategoryNotFoundException(ex.getMessage());
        } catch (OutletNotFoundException ex) {
            throw new OutletNotFoundException(ex.getMessage());
        }
    }
}
