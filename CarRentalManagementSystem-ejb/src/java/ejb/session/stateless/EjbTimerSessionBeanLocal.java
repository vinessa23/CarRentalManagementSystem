/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Reservation;
import java.util.Date;
import javax.ejb.Local;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author YC
 */
@Local
public interface EjbTimerSessionBeanLocal {

    public void allocateCarsToCurrentDayReservations(Date date) throws ReservationNotFoundException;
    
    public void allocateCarsToCurrentDayReservations() throws ReservationNotFoundException;

    public Reservation getReservationForEachCar(Car car) throws ReservationNotFoundException;
    
}
