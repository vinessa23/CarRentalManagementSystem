/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Outlet;
import entity.Reservation;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeFromDifferentOutletException;
import util.exception.EmployeeNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author vinessa
 */
@Remote
public interface TransitSessionBeanRemote {
    public List<Reservation> getTransitRecordsForToday();

    public void assignTransitDriverAutomatically(Outlet outlet) throws EmployeeNotFoundException;

    public void assignTransitDriver(Long reservationId, Long employeeId) throws EmployeeFromDifferentOutletException, EmployeeNotFoundException, ReservationNotFoundException;
    
    public void updateTransitRecordComplete(Long reservationId) throws ReservationNotFoundException;
}
