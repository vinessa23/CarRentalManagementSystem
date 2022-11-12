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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
import util.enumeration.CarStatusEnum;
import util.enumeration.PaymentStatus;
import util.exception.OutletNotOpenYetException;
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
    public Long reserveCar(Long customerId, Packet packet, Long pickupOutletId, Long returnOutletId, Reservation reservation) throws ReservationIdExistException, CustomerNotFoundException, CarNotFoundException, CategoryNotFoundException, OutletNotFoundException, UnknownPersistenceException {
        try {
            System.out.println(customerId);
            Customer customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
            Category category = packet.getCategory();
            Outlet pickupOutlet = outletSessionBeanLocal.retrieveOutletById(pickupOutletId);
            Outlet returnOutlet = outletSessionBeanLocal.retrieveOutletById(returnOutletId);
       
            customer.getReservations().add(reservation);
            reservation.setBookingCustomer(customer);
            reservation.setPickupOutlet(pickupOutlet);
            reservation.setReturnOutlet(returnOutlet);
            reservation.setCategory(category);
            category.getReservations().add(reservation);
            reservation.setTotalAmount(packet.getAmount());
            List<RentalRate> rates = packet.getRentalRates();
            List<RentalRate> distinctRates = new ArrayList<>(new HashSet(rates));
            for (RentalRate r : distinctRates) {
                reservation.getRentalRates().add(r);
                r.getReservations().add(reservation);
            }
   
            if (reservation.getPaymentStatus() == PaymentStatus.UPFRONT) {
                chargeAmountToCC(reservation.getTotalAmount(), reservation.getCcNum(), reservation.getNameOnCard(), reservation.getCvv(), reservation.getExpiryDate());
            }
            em.persist(reservation);
            em.flush();
            
            return reservation.getReservationId();
        }catch (PersistenceException ex){
//            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
//            {
//                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
//                {
//                    throw new ReservationIdExistException("This reservation already exists!");
//                }
//                else
//                {
//                    throw new UnknownPersistenceException(ex.getMessage());
//                }
//            }
//            else
//            {
                throw new UnknownPersistenceException(ex.getMessage());
//            } 
        } catch (CustomerNotFoundException ex) {
            throw new CustomerNotFoundException(ex.getMessage());
        } catch (OutletNotFoundException ex) {
            throw new OutletNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public List<Packet> searchCar(Date start, Date end, Outlet pickupOutlet, Outlet returnOutlet) throws OutletNotOpenYetException{
        //checking whether it is outside the outlet operating hour
        if(pickupOutlet.getOpeningHour() != null) {
            int t1;
            int t2;
            t1 = (int) (pickupOutlet.getOpeningHour().getTime() % 24 * 60 * 60 * 1000L);
            t2 = (int) (start.getTime() % 24 * 60 * 60 * 1000L);
            if(t2 < t1) { //start time is earlier than outlet opening hour
                throw new OutletNotOpenYetException("this outlet has not opened yet for your pickup time");
            }
        } else if (returnOutlet.getClosingHour() != null) {
            int t1;
            int t2;
            t1 = (int) (returnOutlet.getClosingHour().getTime() % 24 * 60 * 60 * 1000L);
            t2 = (int) (end.getTime() % 24 * 60 * 60 * 1000L);
            if(t2 > t1) { //return time is after the outlet closing hour
                throw new OutletNotOpenYetException("this outlet is closed for your return timing");
            }
        }
        List<Category> all = categorySessionBeanLocal.retrieveAllCategories();
        List<Category> availableCategories = categorySessionBeanLocal.categoriesAvailableForThisPeriod(pickupOutlet, start, end);
        List<Packet> res = new ArrayList<>();
        for(Category c : all) {
            if(availableCategories.contains(c)) {
                try {
                    List<RentalRate> r = rentalRateSessionBeanLocal.calculateRentalRate(c, start, end);
                    BigDecimal total = new BigDecimal(0);
                    for(RentalRate rental : r) {
                        total = total.add(rental.getRatePerDay());
                    }
                    Packet p = new Packet(c, r, total);
                    res.add(p);
                } catch (RentalRateNotFoundException ex) { //car is available but rental rate is not found
                    List<RentalRate> r = new ArrayList<>();
                    Packet p = new Packet(c, r, new BigDecimal(0));
                    res.add(p);
                }
            } else { //category is not available for that period
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
    
    //retrieve only the 
    @Override
    public List<Reservation> retrieveAllReservations() throws ReservationNotFoundException{
	Query query = em.createQuery("SELECT r FROM Reservation r");
        try {
            List<Reservation> r = query.getResultList();
//            List<Reservation> res = new ArrayList<>();
//            for(Reservation reservation : r) {
//                if(reservation.getBookingStatus() == BookingStatus.ACTIVE) {
//                    res.add(reservation);
//                }
//            }
//            return res;
            return r;
        } catch (NoResultException ex) {
            throw new ReservationNotFoundException("No reservation found");
        }
    }
    
    @Override
    public List<Reservation> retrieveReservationsOnDate(Date date) throws ReservationNotFoundException {
        try {
            List<Reservation> all = retrieveAllReservations();
            List<Reservation> res = new ArrayList<>();
            LocalDate dateLD = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            for(Reservation r : all) {
                LocalDate reservationLD = r.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if(dateLD.isEqual(reservationLD)) {
                    res.add(r);
                }
            }
            return res;
        } catch (ReservationNotFoundException ex) {
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
    
    @Override
    public String chargeAmountToCC(BigDecimal amount, String ccNum, String nameOnCard, String cvv, Date expiryDate) {
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
    
    @Override
    public void pickupCar(Long reservationId, String pickupCustomerName, String pickupCustomerEmail) throws ReservationNotFoundException {
        try {
            Reservation reservation = getReservation(reservationId);
            if (reservation.getPaymentStatus() == PaymentStatus.PICKUP) {
                chargeAmountToCC(reservation.getTotalAmount(), reservation.getCcNum(), reservation.getNameOnCard(), reservation.getCvv(), reservation.getExpiryDate());
            }
            Car car = reservation.getCar();
            car.setCarStatus(CarStatusEnum.ON_RENTAL);
            reservation.setPickUpCustomerName(pickupCustomerName);
            reservation.setPartnerCustomerEmail(pickupCustomerEmail);
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public void returnCar(Long reservationId, String returnCustomerName, String returnCustomerEmail) throws ReservationNotFoundException {
        try {
            Reservation reservation = getReservation(reservationId);
            Car car = reservation.getCar();
            car.setCarStatus(CarStatusEnum.AVAILABLE);
            reservation.setCar(null);
            reservation.setReturnCustomerName(returnCustomerName);
            reservation.setReturnCustomerEmail(returnCustomerEmail);
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException(ex.getMessage());
        }
    }
    

    
}
