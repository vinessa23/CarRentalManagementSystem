/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsmanagementclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.TransitSessionBeanRemote;
import entity.Employee;
import util.enumeration.EmployeeRoles;
import util.exception.InvalidEmployeeRoleException;

/**
 *
 * @author YC
 */
public class OperationsManagementModule {
    
    private ModelSessionBeanRemote modelSessionBeanRemote;
    private CarSessionBeanRemote carSessionBeanRemote;
    private TransitSessionBeanRemote transitSessionBeanRemote;
    private Employee currentEmployee;

    public OperationsManagementModule() {
    }

    public OperationsManagementModule(ModelSessionBeanRemote modelSessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, TransitSessionBeanRemote transitSessionBeanRemote, Employee currentEmployee) {
        this();
        this.modelSessionBeanRemote = modelSessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.transitSessionBeanRemote = transitSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuOperationsManagement() throws InvalidEmployeeRoleException {
        
        if(currentEmployee.getRole()!= EmployeeRoles.OPERATIONS)
        {
            throw new InvalidEmployeeRoleException("You don't have OPERATIONS rights to access the operations management module.");
        }
    }
}
