/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;

/**
 *
 * @author YC
 */
@Stateless
public class EjbTimerSessionBean implements EjbTimerSessionBeanRemote, EjbTimerSessionBeanLocal {

    @EJB
    private CategorySessionBeanLocal categorySessionBeanLocal;

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

//    @Schedule(hour = "2", minute = "0", second = "0", info = "allocateCarsToCurrentDayReservations")
//    public void allocateCarsToCurrentDayReservations(Date date) {
//        Date start = date;
//        start.setHours(2);
//        start.setMinutes(0);
//        start.setSeconds(0);
//        Date end = start;
//        end.setDate(start.getDate() + 1);
//        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate >= :inStartDate AND r.startDate < :inEndDate");
//        query.setParameter("inStartDate", start);
//        query.setParameter("inEndDate", end);
//        List<Reservation> currentDayReservations = query.getResultList();
//
//        for (Reservation r : currentDayReservations) {
//            List<Car> cars = categorySessionBeanLocal.retrieveCarsByCategoryId(r.getCategory().getCategoryId());
//
//            for (Car car : cars) {
//                if (car.getCarStatus() == CarStatusEnum.AVAILABLE && car.getOutlet().getOutletId().equals(r.getPickupOutlet().getOutletId())) {
//                    r.setCar(car);
//                } else if (car.getCarStatus() == CarStatusEnum.AVAILABLE) {
////                     not done: need to consider cars available in other outlet, cars on rent which will return to same outlet before new 
////                     reservation begins and also those on rent but return to diff outlet + transit time < start of new reservation
//                }
//            }
//        }
//    }
}
