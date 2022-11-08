/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.RentalRateSessionBeanLocal;
import entity.Category;
import entity.Outlet;
import entity.RentalRate;
import entity.Reservation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.BookingStatus;
import util.enumeration.PaymentStatus;
import util.exception.RentalRateNotFoundException;
import util.exception.ReservationAlreadyCancelledException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author vinessa
 */
@Stateful
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "RentalRateSessionBeanLocal")
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

    @EJB(name = "CategorySessionBeanLocal")
    private CategorySessionBeanLocal categorySessionBeanLocal;
    
    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    
    public List<Pair<Category, List<RentalRate>>> searchCar(Date start, Date end, Outlet pickupOutlet, Outlet returnOutlet) {
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
    public void cancelReservation(Long reservationId, Date cancellationDate) throws ReservationAlreadyCancelledException, ReservationNotFoundException{
        try {
            Reservation r = getReservation(reservationId);
            if(r.getBookingStatus() == BookingStatus.CANCELLED) {
                throw new ReservationAlreadyCancelledException("you already cancelled this reservation");
            }
            
            if(r.getPaymentStatus() == PaymentStatus.UPFRONT) {
                //refund after deducting
                
            } else {
                //charge credit card
            }
            
            r.setBookingStatus(BookingStatus.CANCELLED);
            r.setCancellationTime(cancellationDate);
            r.setPaymentStatus(PaymentStatus.REFUNDED);
            
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException(ex.getMessage());
        }
    }
    
    private BigDecimal calculatePenaltyAmount(Reservation reservation, Date cancellationDate) {
        //calculate time difference in miliseconds (based on Java Date getTime())
        Date startDate = reservation.getStartDate();
        long timeDifference = startDate.getTime() - cancellationDate.getTime();
        long hourDifference = (timeDifference/ (1000 * 60 * 60)) % 24;
        Date fourteenDaysBeforePickUp = plusHours(startDate, (-14 * 24));
        Date sevenDaysBeforePickUp = plusHours(startDate, (-7 * 24));
        
        if(cancellationDate.before(fourteenDaysBeforePickUp)) {
        
        } else if (cancellationDate.before(sevenDaysBeforePickUp)) {
            
        } else {
            
        }
    } 
    
        private Date plusHours(Date date, int hour) {
        LocalDateTime initialLDT = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime afterLDT = initialLDT.plusHours(hour);
        return java.sql.Timestamp.valueOf(afterLDT);
    }
    
    
}
