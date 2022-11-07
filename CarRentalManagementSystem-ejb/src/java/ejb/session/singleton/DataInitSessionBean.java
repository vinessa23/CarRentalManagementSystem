/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ModelSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import entity.Category;
import entity.Employee;
import entity.Model;
import entity.Outlet;
import java.util.Date;
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
import util.exception.CategoryNameExistException;
import util.exception.CategoryNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.ModelNameExistException;
import util.exception.OutletNameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB(name = "ModelSessionBeanLocal")
    private ModelSessionBeanLocal modelSessionBeanLocal;

    @EJB(name = "CategorySessionBeanLocal")
    private CategorySessionBeanLocal categorySessionBeanLocal;

    @EJB(name = "OutletSessionBeanLocal")
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    
    

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;
    
    @PostConstruct
    public void postConstruct()
    {
        //@SHINO i think it is better to check outlet instead of employee here, once you alr done the outlet class
        if(em.find(Outlet.class, 1l) == null) {
            initializeData();
        }
    }
    
    private void initializeData() {
        try {
            Outlet a = new Outlet("Outlet A");
            Long aId = outletSessionBeanLocal.createNewOutlet(a);
            Outlet b = new Outlet("Outlet B");
            Long bId = outletSessionBeanLocal.createNewOutlet(b);
            Outlet c = new Outlet("Outlet C", new Date(0,0,0,10,0),new Date(0,0,0,22,0));
            Long cId = outletSessionBeanLocal.createNewOutlet(c);

            //initialising employee, 1 per access right for each outlet? or only system admin 
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee A1", EmployeeRoles.SALES), aId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee A2", EmployeeRoles.OPERATIONS), aId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee A3", EmployeeRoles.CUSTOMERSERVICE), aId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee A4", EmployeeRoles.EMPLOYEE), aId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee A5", EmployeeRoles.EMPLOYEE), aId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee B1", EmployeeRoles.SALES), bId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee B2", EmployeeRoles.OPERATIONS), bId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee B3", EmployeeRoles.CUSTOMERSERVICE), bId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee C1", EmployeeRoles.OPERATIONS), cId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee C2", EmployeeRoles.CUSTOMERSERVICE), cId);
            
            //initialise category, model and car here
            Category catA = new Category("Standard Sedan");
            Long catAId = categorySessionBeanLocal.createNewCategory(catA);
            Long catBId = categorySessionBeanLocal.createNewCategory(new Category("Family Sedan"));
            Long catCId = categorySessionBeanLocal.createNewCategory(new Category("Luxury Sedan"));
            Long catDId = categorySessionBeanLocal.createNewCategory(new Category("SUV and Minivan"));
            
            modelSessionBeanLocal.createNewModel(catAId, new Model("Toyota", "Corolla", true));
            modelSessionBeanLocal.createNewModel(catAId, new Model("Honda", "Civic", true));
            modelSessionBeanLocal.createNewModel(catAId, new Model("Nissan", "Sunny", true));
            modelSessionBeanLocal.createNewModel(catCId, new Model("Mercedes", "E Class", true));
            modelSessionBeanLocal.createNewModel(catCId, new Model("BMW", "5 Series", true));
            modelSessionBeanLocal.createNewModel(catCId, new Model("Audi", "A6", true));

        } catch (EmployeeUsernameExistException | UnknownPersistenceException ex ) {
            System.out.println(ex.getMessage());
        } catch (OutletNameExistException ex) {
            System.out.println(ex.getMessage());
        } catch (CategoryNameExistException ex) {
            System.out.println(ex.getMessage());
        } catch (ModelNameExistException ex) {
            System.out.println(ex.getMessage());
        } catch (CategoryNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
        

}
