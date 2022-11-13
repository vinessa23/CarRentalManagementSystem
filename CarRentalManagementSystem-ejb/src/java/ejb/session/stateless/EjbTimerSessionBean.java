/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Reservation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author YC
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB
    private CategorySessionBeanLocal categorySessionBeanLocal;

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    @Override
    public void allocateCarsToCurrentDayReservations(Date date) throws ReservationNotFoundException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Date start = calendar.getTime();
        Calendar endCalendar = calendar;
        endCalendar.set(Calendar.DATE, date.getDate() + 1);
        Date end = endCalendar.getTime();
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate >= :inStartDate AND r.startDate < :inEndDate");
        query.setParameter("inStartDate", start);
        query.setParameter("inEndDate", end);
        List<Reservation> currentDayReservations = query.getResultList();
        List<Reservation> requireTransitReservations = new ArrayList<>();

        for (Reservation r : currentDayReservations) {
            List<Car> cars = categorySessionBeanLocal.retrieveCarsByCategoryId(r.getCategory().getCategoryId());

            for (Car car : cars) {
                if (car.getCarStatus() == CarStatusEnum.AVAILABLE && getReservationForEachCar(car) == null 
                        && car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())) {
                    r.setCar(car);
                    break;
                } 
            }
            
            if (r.getCar() != null) {
                continue;
            }
            
            for (Car car : cars) {
                if (car.getCarStatus() == CarStatusEnum.ON_RENTAL && car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())
                        && getReservationForEachCar(car).getEndDate().getTime() <= r.getStartDate().getTime() - 7200000) {
                    r.setCar(car);
                    break;
                }
            }
            
            if (r.getCar() != null) {
                continue;
            }
            
            for (Car car : cars) {
                if (car.getCarStatus() == CarStatusEnum.AVAILABLE && getReservationForEachCar(car) == null
                        && !car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())) {
                    requireTransitReservations.add(r);
                    r.setNeedTransit(true);
                    r.setCar(car);
                    break;
                }
            }
            
            if (r.getCar() != null) {
                continue;
            }
            
            for (Car car : cars) {
                if (car.getCarStatus() == CarStatusEnum.ON_RENTAL && !car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())
                        && getReservationForEachCar(car).getEndDate().getTime() <= r.getStartDate().getTime() - 7200000) {
                    requireTransitReservations.add(r);
                    r.setNeedTransit(true);
                    r.setCar(car);
                    break;
                }
            }
        }
        generateTransitDriverDispatchRecord(date, requireTransitReservations);
    }
    
    @Override
    public Reservation getReservationForEachCar(Car car) throws ReservationNotFoundException {
        List<Reservation> reservations = reservationSessionBeanLocal.retrieveAllReservations();
        Reservation reservation = null;
        for (Reservation r : reservations) {
            if (r.getCar() != null) {
                if (r.getCar().getCarId().equals(car.getCarId())) {
                    reservation = r;
                    return reservation;
                }
            }
        }
        return reservation;
    }
    
    @Schedule(hour = "2", minute = "0", second = "0", info = "allocateCarsToCurrentDayReservations")
    @Override
    public void allocateCarsToCurrentDayReservations() throws ReservationNotFoundException {
        Date start = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.set(Calendar.DATE, start.getDate() + 1);
        Date end = calendar.getTime();
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate >= :inStartDate AND r.startDate < :inEndDate");
        query.setParameter("inStartDate", start);
        query.setParameter("inEndDate", end);
        List<Reservation> currentDayReservations = query.getResultList();
        List<Reservation> requireTransitReservations = new ArrayList<>();

        for (Reservation r : currentDayReservations) {
            List<Car> cars = categorySessionBeanLocal.retrieveCarsByCategoryId(r.getCategory().getCategoryId());

            for (Car car : cars) {
                if (car.getCarStatus() == CarStatusEnum.AVAILABLE && getReservationForEachCar(car) == null 
                        && car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())) {
                    r.setCar(car);
                    break;
                } 
            }
            
            if (r.getCar() != null) {
                continue;
            }
            
            for (Car car : cars) {
                if (car.getCarStatus() == CarStatusEnum.ON_RENTAL && car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())
                        && getReservationForEachCar(car).getEndDate().getTime() <= r.getStartDate().getTime() - 7200000) {
                    r.setCar(car);
                    break;
                }
            }
            
            if (r.getCar() != null) {
                continue;
            }
            
            for (Car car : cars) {
                if (car.getCarStatus() == CarStatusEnum.AVAILABLE && getReservationForEachCar(car) == null
                        && !car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())) {
                    requireTransitReservations.add(r);
                    r.setNeedTransit(true);
                    r.setCar(car);
                    break;
                }
            }
            
            if (r.getCar() != null) {
                continue;
            }
            
            for (Car car : cars) {
                if (car.getCarStatus() == CarStatusEnum.ON_RENTAL && !car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())
                        && getReservationForEachCar(car).getEndDate().getTime() <= r.getStartDate().getTime() - 7200000) {
                    requireTransitReservations.add(r);
                    r.setNeedTransit(true);
                    r.setCar(car);
                    break;
                }
            }
        }
        generateTransitDriverDispatchRecord(start, requireTransitReservations);
    }
    
    @Override
    public List<Reservation> generateTransitDriverDispatchRecord(Date date, List<Reservation> reservations) {
        return reservations;
    }
}
