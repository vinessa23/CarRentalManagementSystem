/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Outlet;
import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmployeeFromDifferentOutletException;
import util.exception.EmployeeNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.TransitRecordNotFoundException;

/**
 *
 * @author vinessa
 */
@Local
public interface TransitSessionBeanLocal {

    public List<Reservation> getTransitRecordsForToday(Long employeeId) throws EmployeeNotFoundException;
    
    public List<Reservation> getTransitRecordsForToday(Long employeeId, Date date) throws EmployeeNotFoundException;

    public void assignTransitDriverAutomatically(Long employeeId, Outlet outlet) throws EmployeeNotFoundException;

    public void assignTransitDriver(Long reservationId, Long employeeId) throws EmployeeFromDifferentOutletException, EmployeeNotFoundException, ReservationNotFoundException;

    public void updateTransitRecordComplete(Long employeeId, Long reservationId) throws TransitRecordNotFoundException, ReservationNotFoundException, EmployeeNotFoundException;
    
}
