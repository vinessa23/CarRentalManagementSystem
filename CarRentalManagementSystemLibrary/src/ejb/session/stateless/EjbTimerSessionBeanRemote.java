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
import javax.ejb.Remote;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author YC
 */
@Remote
public interface EjbTimerSessionBeanRemote {
    
    public void allocateCarsToCurrentDayReservations(Date date) throws ReservationNotFoundException;
    
    public void allocateCarsToCurrentDayReservations() throws ReservationNotFoundException;

    public Reservation getReservationForEachCar(Car car) throws ReservationNotFoundException;
    
    public List<Reservation> generateTransitDriverDispatchRecord(Date date, List<Reservation> reservations);
    
}
