/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Category;
import entity.Customer;
import entity.Outlet;
import entity.RentalRate;
import entity.Reservation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.OutletNotFoundException;
import javax.persistence.Query;
import util.enumeration.BookingStatus;
import util.enumeration.PaymentStatus;
import util.exception.RentalRateNotFoundException;
import util.exception.ReservationAlreadyCancelledException;
import util.exception.ReservationNotFoundException;
import util.exception.ReservationIdExistException;
import util.exception.UnknownPersistenceException;
import util.helperClass.Packet;

/**
 *
 * @author vinessa
 */
@Stateless
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
    
    @Override
    public Long reserveCar(Long customerId, Long carId, Long categoryId, Long pickupOutletId, Long returnOutletId, Reservation reservation) throws ReservationIdExistException, CustomerNotFoundException, CarNotFoundException, CategoryNotFoundException, OutletNotFoundException, UnknownPersistenceException {
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
    
    @Override
    public List<Packet> searchCar(Category category, Date start, Date end, Outlet pickupOutlet, Outlet returnOutlet) {
        List<Category> categories = categorySessionBeanLocal.categoriesAvailableForThisPeriod(pickupOutlet, start, end);
        List<Packet> res = new ArrayList<>();
        for(Category c : categories) {
            try {
                List<RentalRate> r = rentalRateSessionBeanLocal.calculateRentalRate(c, start, end);
                BigDecimal total = new BigDecimal(0);
                for(RentalRate rental : r) {
                    total = total.add(rental.getRatePerDay());
                }
                Packet p = new Packet(c, r, total);
            } catch (RentalRateNotFoundException ex) {
                List<RentalRate> r = new ArrayList<>();
                Packet p = new Packet(c, r, new BigDecimal(0));
                res.add(p);
            }
        }
        return res;
    }
    
    @Override
    public Reservation getReservation(Long id) throws ReservationNotFoundException {
        Reservation r = em.find(Reservation.class, id);
        if(r != null) {
            return r;
        } else {
            throw new ReservationNotFoundException("Reservation ID " + id + " does not exist!");
        }
    }
    
    @Override
    public List<Reservation> retrieveAllReservations() throws ReservationNotFoundException{
	Query query = em.createQuery("SELECT r FROM Reservation r");
        try {
            List<Reservation> r = query.getResultList();
            return r;
        } catch (NoResultException ex) {
            throw new ReservationNotFoundException("No reservation found");
        }
    }
    
    @Override
    public String cancelReservation(Long reservationId, Date cancellationDate) throws ReservationAlreadyCancelledException, ReservationNotFoundException{
        try {
            Reservation r = getReservation(reservationId);
            if(r.getBookingStatus() == BookingStatus.CANCELLED) {
                throw new ReservationAlreadyCancelledException("you already cancelled this reservation");
            }
            BigDecimal reservationAmount = r.getTotalAmount();
            BigDecimal penaltyAmount = calculatePenaltyAmount(r, cancellationDate);
            String transactionId;
            if(r.getPaymentStatus() == PaymentStatus.UPFRONT) {
                //refund after deducting
                BigDecimal refundAmount = reservationAmount.subtract(penaltyAmount);
                transactionId = debitAmountToCC(refundAmount, r.getCcNum(), r.getNameOnCard(), r.getCvv(), r.getExpiryDate());
            } else {
                //charge credit card the penalty
                transactionId = chargeAmountToCC(penaltyAmount, r.getCcNum(), r.getNameOnCard(), r.getCvv(), r.getExpiryDate());
            }
            
            r.setBookingStatus(BookingStatus.CANCELLED);
            r.setCancellationTime(cancellationDate);
            r.setPaymentStatus(PaymentStatus.REFUNDED);
            
            return transactionId;     
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException(ex.getMessage());
        }
    }
    
    private BigDecimal calculatePenaltyAmount(Reservation reservation, Date cancellationDate) {
        //calculate time difference in miliseconds (based on Java Date getTime())
        Date startDate = reservation.getStartDate();
//        long timeDifference = startDate.getTime() - cancellationDate.getTime();
//        long hourDifference = (timeDifference / (1000 * 60 * 60)) % 24;
        Date fourteenDaysBeforePickUp = plusHours(startDate, (-14 * 24));
        Date sevenDaysBeforePickUp = plusHours(startDate, (-7 * 24));
        Date threeDaysBeforePickUp = plusHours(startDate, (-3 * 24));
        BigDecimal reservationAmount = reservation.getTotalAmount();
        
        if(cancellationDate.before(fourteenDaysBeforePickUp)) {
            return new BigDecimal(0);
        } else if (cancellationDate.before(sevenDaysBeforePickUp)) {
            return reservationAmount.multiply(new BigDecimal(20)).divide(new BigDecimal(100));
        } else if (cancellationDate.before(threeDaysBeforePickUp)) {
            return reservationAmount.multiply(new BigDecimal(50)).divide(new BigDecimal(100));
        } else {
            return reservationAmount.multiply(new BigDecimal(70)).divide(new BigDecimal(100));
        }
    } 
    
    private Date plusHours(Date date, int hour) {
        LocalDateTime initialLDT = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime afterLDT = initialLDT.plusHours(hour);
        return java.sql.Timestamp.valueOf(afterLDT);
    }
    
    private String chargeAmountToCC(BigDecimal amount, String ccNum, String nameOnCard, String cvv, Date expiryDate) {
        System.out.println("Processing payment....");
        String paymentId = generateRandomNumber();
        System.out.println("Processing successful. Transaction ID: " + paymentId);
        return paymentId;
    }
    
    private String debitAmountToCC(BigDecimal amount, String ccNum, String nameOnCard, String cvv, Date expiryDate) {
        System.out.println("Processing refund....");
        String paymentId = generateRandomNumber();
        System.out.println("Refund successful. Transaction ID: " + paymentId);
        return paymentId;
    }
    
    private String generateRandomNumber() {
        int leftLimit = 48; 
        int rightLimit = 57;
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) 
              (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        
        return generatedString;
    }
    

    
}
