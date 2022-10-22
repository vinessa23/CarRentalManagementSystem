/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeRoles;
import util.exception.EmployeeUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;
    
    @PostConstruct
    public void postConstruct()
    {
        //@SHINO i think it is better to check outlet instead of employee here, once you alr done the outlet class
        if(em.find(Employee.class, 1l) == null) {
            initializeData();
        }
    }
    
    private void initializeData() {
        try {
            //@SHINO must create new outlet here and save the id so i can pass in to employee ltr
            
            //initialising employee, 1 per access right for each outlet? or only system admin 
            //TODO: associations
//            employeeSessionBeanLocal.createNewEmployee(new Employee("sales", "manager 1", "sm1", "password", EmployeeRoles.SALES));
//            employeeSessionBeanLocal.createNewEmployee(new Employee("operations", "manager 1", "om1", "password", EmployeeRoles.OPERATIONS));
//            employeeSessionBeanLocal.createNewEmployee(new Employee("customer", "service 1", "cs1", "password", EmployeeRoles.CUSTOMERSERVICE));
            employeeSessionBeanLocal.createNewEmployee(new Employee("system", "admin", "sa1", "password", EmployeeRoles.SYSTEMADMIN));   
        } catch (EmployeeUsernameExistException | UnknownPersistenceException ex ) {
            ex.printStackTrace();
        }
    }
        

}
