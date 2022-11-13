/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ModelSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.RentalRateSessionBeanLocal;
import entity.Car;
import entity.Category;
import entity.Customer;
import entity.Employee;
import entity.Model;
import entity.Outlet;
import entity.RentalRate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import util.enumeration.CustomerType;
import util.enumeration.EmployeeRoles;
import util.enumeration.RentalRateType;
import util.exception.CarLicensePlateExistException;
import util.exception.CategoryNameExistException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerEmailExistException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
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

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB(name = "RentalRateSessionBeanLocal")
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

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
            Outlet c = new Outlet("Outlet C", new Date(1,1,1,8,0),new Date(1,1,1,22,0));
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
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee C1", EmployeeRoles.SALES), cId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee C2", EmployeeRoles.OPERATIONS), cId);
            employeeSessionBeanLocal.createNewEmployee(new Employee("Employee C3", EmployeeRoles.CUSTOMERSERVICE), cId);
            
            //initialise category, model and car here
            Long catAId = categorySessionBeanLocal.createNewCategory(new Category("Standard Sedan"));
            Long catBId = categorySessionBeanLocal.createNewCategory(new Category("Family Sedan"));
            Long catCId = categorySessionBeanLocal.createNewCategory(new Category("Luxury Sedan"));
            Long catDId = categorySessionBeanLocal.createNewCategory(new Category("SUV and Minivan"));
            
            Long modelAId = modelSessionBeanLocal.createNewModel(catAId, new Model("Toyota", "Corolla", true));
            Long modelBId = modelSessionBeanLocal.createNewModel(catAId, new Model("Honda", "Civic", true));
            Long modelCId = modelSessionBeanLocal.createNewModel(catAId, new Model("Nissan", "Sunny", true));
            Long modelDId = modelSessionBeanLocal.createNewModel(catCId, new Model("Mercedes", "E Class", true));
            Long modelEId = modelSessionBeanLocal.createNewModel(catCId, new Model("BMW", "5 Series", true));
            Long modelFId = modelSessionBeanLocal.createNewModel(catCId, new Model("Audi", "A6", true));
            
            carSessionBeanLocal.createNewCar(aId, modelAId, new Car("SS00A1TC", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(aId, modelAId, new Car("SS00A2TC", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(aId, modelAId, new Car("SS00A3TC", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(bId, modelBId, new Car("SS00B1HC", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(bId, modelBId, new Car("SS00B2HC", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(bId, modelBId, new Car("SS00B3HC", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(cId, modelCId, new Car("SS00C1NS", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(cId, modelCId, new Car("SS00C2NS", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(cId, modelCId, new Car("SS00C3NS", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(aId, modelDId, new Car("LS00A4ME", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(bId, modelEId, new Car("LS00B4B5", CarStatusEnum.AVAILABLE, true));
            carSessionBeanLocal.createNewCar(cId, modelFId, new Car("LS00C4A6", CarStatusEnum.AVAILABLE, true));
            
            //initialising with Date constructor give an error (+1900 to the year value)
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("Standard Sedan - Default", new BigDecimal("100"), true, RentalRateType.DEFAULT), catAId);
            LocalDateTime startLdt1 = LocalDateTime.of(2022, 12, 9, 12, 0);
            Date start1 = Date.from(startLdt1.atZone(ZoneId.systemDefault()).toInstant());
            LocalDateTime endLdt1 = LocalDateTime.of(2022, 12, 11, 0, 0);
            Date end1 = Date.from(endLdt1.atZone(ZoneId.systemDefault()).toInstant());
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("Standard Sedan - Weekend Promo", new BigDecimal("80"), true, start1, end1, RentalRateType.PROMOTION), catAId);
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("Family Sedan - Default", new BigDecimal("200"), true, RentalRateType.DEFAULT), catBId);
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("Luxury Sedan - Default", new BigDecimal("300"), true, RentalRateType.DEFAULT), catCId);
            LocalDateTime startLdt2 = LocalDateTime.of(2022, 12, 5, 0, 0);
            Date start2 = Date.from(startLdt2.atZone(ZoneId.systemDefault()).toInstant());
            LocalDateTime endLdt2 = LocalDateTime.of(2022, 12, 5, 23, 59);
            Date end2 = Date.from(endLdt2.atZone(ZoneId.systemDefault()).toInstant());
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("Luxury Sedan - Monday", new BigDecimal("310"), true, start2, end2, RentalRateType.PEAK), catCId);
            LocalDateTime startLdt3 = LocalDateTime.of(2022, 12, 6, 0, 0);
            Date start3 = Date.from(startLdt3.atZone(ZoneId.systemDefault()).toInstant());
            LocalDateTime endLdt3 = LocalDateTime.of(2022, 12, 6, 23, 59);
            Date end3 = Date.from(endLdt3.atZone(ZoneId.systemDefault()).toInstant());
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("Luxury Sedan - Tuesday", new BigDecimal("320"), true, start3, end3, RentalRateType.PEAK), catCId);
            LocalDateTime startLdt4 = LocalDateTime.of(2022, 12, 7, 0, 0);
            Date start4 = Date.from(startLdt4.atZone(ZoneId.systemDefault()).toInstant());
            LocalDateTime endLdt4 = LocalDateTime.of(2022, 12, 7, 23, 59);
            Date end4 = Date.from(endLdt4.atZone(ZoneId.systemDefault()).toInstant());
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("Luxury Sedan - Wednesday", new BigDecimal("330"), true, start4, end4, RentalRateType.PEAK), catCId);
            LocalDateTime startLdt5 = LocalDateTime.of(2022, 12, 7, 12, 0);
            Date start5 = Date.from(startLdt5.atZone(ZoneId.systemDefault()).toInstant());
            LocalDateTime endLdt5 = LocalDateTime.of(2022, 12, 8, 12, 0);
            Date end5 = Date.from(endLdt5.atZone(ZoneId.systemDefault()).toInstant());
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("Luxury Sedan - Weekday Promo", new BigDecimal("250"), true, start5, end5, RentalRateType.PROMOTION), catCId);
            rentalRateSessionBeanLocal.createNewRentalRate(new RentalRate("SUV and Minivan - Default", new BigDecimal("400"), true, RentalRateType.DEFAULT), catDId);
            
            customerSessionBeanLocal.createNewCustomer(new Customer("Holiday.com", CustomerType.PARTNER));
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
        } catch (CustomerEmailExistException ex) {
            System.out.println(ex.getMessage());
        } catch (InputDataValidationException ex) {
            System.out.println(ex.getMessage());
        }
    }
        

}
