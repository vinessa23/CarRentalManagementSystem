/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ModelSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import entity.Car;
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
import util.enumeration.CarStatusEnum;
import util.enumeration.EmployeeRoles;
import util.exception.CarLicensePlateExistException;
import util.exception.CategoryNameExistException;
import util.exception.CategoryNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.ModelIsNotEnabledException;
import util.exception.ModelNameExistException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNameExistException;
import util.exception.OutletNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBeanLocal;

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
            
            Long modelAId = modelSessionBeanLocal.createNewModel(catAId, new Model("Toyota", "Corolla", true));
            Long modelBId = modelSessionBeanLocal.createNewModel(catAId, new Model("Honda", "Civic", true));
            Long modelCId = modelSessionBeanLocal.createNewModel(catAId, new Model("Nissan", "Sunny", true));
            Long modelDId = modelSessionBeanLocal.createNewModel(catCId, new Model("Mercedes", "E Class", true));
            Long modelEId = modelSessionBeanLocal.createNewModel(catCId, new Model("BMW", "5 Series", true));
            Long modelFId = modelSessionBeanLocal.createNewModel(catCId, new Model("Audi", "A6", true));
            
            carSessionBeanLocal.createNewCar(aId, modelAId, new Car("SS00A1TC", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(aId, modelAId, new Car("SS00A2TC", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(aId, modelAId, new Car("SS00A3TC", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(bId, modelBId, new Car("SS00B1HC", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(bId, modelBId, new Car("SS00B2HC", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(bId, modelBId, new Car("SS00B3HC", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(cId, modelCId, new Car("SS00C1NS", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(cId, modelCId, new Car("SS00C2NS", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(cId, modelCId, new Car("SS00C3NS", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(aId, modelDId, new Car("LS00A4ME", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(bId, modelEId, new Car("LS00B4B5", CarStatusEnum.IN_OUTLET, true));
            carSessionBeanLocal.createNewCar(cId, modelFId, new Car("LS00C4A6", CarStatusEnum.IN_OUTLET, true));
            
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
        } catch (OutletNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (ModelIsNotEnabledException ex) {
            System.out.println(ex.getMessage());
        } catch (ModelNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (CarLicensePlateExistException ex) {
            System.out.println(ex.getMessage());
        }
    }
        

}
