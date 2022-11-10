/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Outlet;
import entity.Reservation;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeFromDifferentOutletException;
import util.exception.EmployeeNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author vinessa
 */
@Stateless
public class TransitSessionBean implements TransitSessionBeanRemote, TransitSessionBeanLocal {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @EJB(name = "OutletSessionBeanLocal")
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;
    
    //let client choose employee from this reservation pickup outlet only, so the employee not found should not be thrown actly
    //let client choose reservation that only need transit, but this attribute is currently deleted (ASK SHINO)
    public void assignTransitDriver(Long reservationId, Long employeeId) throws EmployeeFromDifferentOutletException, EmployeeNotFoundException, ReservationNotFoundException {
        try {
            Reservation reservation = reservationSessionBeanLocal.getReservation(reservationId);
            Employee employee = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);
            
            if(employee.getOutlet() == reservation.getPickupOutlet()) {
                reservation.setDriver(employee);
            } else {   
                throw new EmployeeFromDifferentOutletException("employee cannot be assigned to this transit record as he/she from different outlet");
            }
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException(ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException(ex.getMessage());
        }
    }
    


    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
