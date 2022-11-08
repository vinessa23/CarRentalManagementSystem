/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.RentalRateSessionBeanLocal;
import entity.Car;
import entity.Category;
import entity.Customer;
import entity.Outlet;
import entity.RentalRate;
import entity.Reservation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.enumeration.CarStatusEnum;
import util.enumeration.PaymentStatus;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.RentalRateNotFoundException;
import util.exception.ReservationIdExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Stateful
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

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
    public List<Pair<Category, List<RentalRate>>> searchCar(Category category, Date start, Date end, Outlet pickupOutlet, Outlet returnOutlet) {
        List<Category> categories = categorySessionBeanLocal.categoriesAvailableForThisPeriod(pickupOutlet, start, end);
        List<Pair<Category, List<RentalRate>>> res = new ArrayList<>();
        for(Category c : categories) {
            try {
                List<RentalRate> r = rentalRateSessionBeanLocal.calculateRentalRate(c, start, end);
                Pair<Category, List<RentalRate>> pair = new Pair<Category, List<RentalRate>>(c, r);
                res.add(pair);
            } catch (RentalRateNotFoundException ex) {
                List<RentalRate> r = new ArrayList<>();
                Pair<Category, List<RentalRate>> pair = new Pair<Category, List<RentalRate>>(c, r);
                res.add(pair);
            }
        }
        return res;
    }
    
    public void pickupCar(Long reservationId) {
//        Reservation reservation;
//        if (reservation.getPaymentStatus() == PaymentStatus.PICKUP) {
//            //make payment
//        }
//        Car car = reservation.getCar();
//        car.setCarStatus(CarStatusEnum.ON_RENTAL);
    }
    
    public void returnCar(Reservation reservation) {
        Car car = reservation.getCar();
        car.setCarStatus(CarStatusEnum.IN_OUTLET);
    }
}
