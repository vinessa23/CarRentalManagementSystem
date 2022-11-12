/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsmanagementclient;

import ejb.session.stateless.RentalRateSessionBeanRemote;
import entity.Employee;
import util.enumeration.EmployeeRoles;
import util.exception.InvalidEmployeeRoleException;

/**
 *
 * @author YC
 */
public class SalesManagementModule {

    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    private Employee currentEmployee;

    public SalesManagementModule() {
        
    }

    public SalesManagementModule(RentalRateSessionBeanRemote rentalRateSessionBeanRemote, Employee currentEmployee) {
        this();
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuSalesManagement() throws InvalidEmployeeRoleException {
        
        if(currentEmployee.getRole()!= EmployeeRoles.SALES)
        {
            throw new InvalidEmployeeRoleException("You don't have SALES rights to access the sales management module.");
        }
    }
    
}
