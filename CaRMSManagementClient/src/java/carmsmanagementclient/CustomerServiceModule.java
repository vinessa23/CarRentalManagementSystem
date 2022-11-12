/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsmanagementclient;

import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Employee;
import util.enumeration.EmployeeRoles;
import util.exception.InvalidEmployeeRoleException;

/**
 *
 * @author YC
 */
public class CustomerServiceModule {
    
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private Employee currentEmployee;

    public CustomerServiceModule() {
    }

    public CustomerServiceModule(ReservationSessionBeanRemote reservationSessionBeanRemote, Employee currentEmployee) {
        this();
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuCustomerService() throws InvalidEmployeeRoleException {
        
        if(currentEmployee.getRole()!= EmployeeRoles.CUSTOMERSERVICE)
        {
            throw new InvalidEmployeeRoleException("You don't have CUSTOMER SERVICE rights to access the customer service module.");
        }
    }
}
