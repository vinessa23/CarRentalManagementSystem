/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Outlet;
import entity.Reservation;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;
import util.exception.EmployeeFromDifferentOutletException;
import util.exception.EmployeeNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.TransitRecordNotFoundException;

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
    
    
    @Override
    public List<Reservation> getTransitRecordsForToday(Long employeeId) throws EmployeeNotFoundException {
        try {
            //warning: error might arise from this conversion
            LocalDate todayLDT = LocalDate.now();
            List<Reservation> res = new ArrayList<>();
            Date today = Date.from(todayLDT.atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Reservation> reservationsToday = reservationSessionBeanLocal.retrieveReservationsOnDate(today);
            Employee employee = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);
            for(Reservation r : reservationsToday) {
                if(r.isNeedTransit() && r.getPickupOutlet().equals(employee.getOutlet())) {
                    res.add(r);
                }
            }
            return res;
        } catch (ReservationNotFoundException ex) {
            return new ArrayList<>(); //no transit record for today
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public List<Reservation> getTransitRecordsForToday(Long employeeId, Date date) throws EmployeeNotFoundException {
        try {
            List<Reservation> res = new ArrayList<>();
            List<Reservation> reservationsToday = reservationSessionBeanLocal.retrieveReservationsOnDate(date);
            Employee employee = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);
            for(Reservation r : reservationsToday) {
                if(r.isNeedTransit() && r.getPickupOutlet().equals(employee.getOutlet())) {
                    res.add(r);
                }
            }
            return res;
        } catch (ReservationNotFoundException ex) {
            return new ArrayList<>(); //no transit record for today
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public void assignTransitDriverAutomatically(Long employeeId, Outlet outlet) throws EmployeeNotFoundException {
        try {
            List<Reservation> reservations = getTransitRecordsForToday(employeeId);
            List<Employee> employees = employeeSessionBeanLocal.retrieveEmployeesFromOutlet(outlet);
            int i = 0;
            for(Reservation r : reservations) {
                r.setDriver(employees.get(i));
                i++;
                if(i >= employees.size()) {
                    i = i % employees.size();
                }
            }
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException("No employees from this outlet can be assigned");
        }
        
    }
    
    //let client choose employee from this reservation pickup outlet only, so the employee not found should not be thrown actly
    //let client choose reservation that only need transit, but this attribute is currently deleted (ASK SHINO)
    @Override
    public void assignTransitDriver(Long reservationId, Long employeeId) throws EmployeeFromDifferentOutletException, EmployeeNotFoundException, ReservationNotFoundException {
        try {
            Reservation reservation = reservationSessionBeanLocal.getReservation(reservationId);
            Employee employee = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);
            
            if(employee.getOutlet() == reservation.getPickupOutlet()) {
                reservation.setDriver(employee);
            } else {   
                throw new EmployeeFromDifferentOutletException("Employee cannot be assigned to this transit record as he/she is from different outlet");
            }
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException(ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException(ex.getMessage());
        }
    }
    
    @Override
    public void updateTransitRecordComplete(Long employeeId, Long reservationId) throws TransitRecordNotFoundException, ReservationNotFoundException, EmployeeNotFoundException{
        try {
            Reservation reservation = reservationSessionBeanLocal.getReservation(reservationId);
            Employee employee = employeeSessionBeanLocal.retrieveEmployeeById(employeeId);
            if (reservation.isNeedTransit() && reservation.getPickupOutlet().equals(employee.getOutlet())) {
                reservation.setIsTransitCompleted(true);
                reservation.getCar().setOutlet(reservation.getPickupOutlet());
                reservation.getCar().setCarStatus(CarStatusEnum.AVAILABLE);
            } else if (!reservation.isNeedTransit()) {
                throw new TransitRecordNotFoundException("No transit record found!");
            }
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException(ex.getMessage());
        } catch (EmployeeNotFoundException ex) {
            throw new EmployeeNotFoundException(ex.getMessage());
        }
    }
}
