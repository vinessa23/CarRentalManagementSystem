/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsmanagementclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.EjbTimerSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.TransitSessionBeanRemote;
import entity.Employee;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import util.exception.InvalidEmployeeRoleException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author YC
 */
public class MainApp {

    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    private ModelSessionBeanRemote modelSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private CarSessionBeanRemote carSessionBeanRemote;
    private OutletSessionBeanRemote outletSessionBeanRemote; 
    private TransitSessionBeanRemote transitSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private EjbTimerSessionBeanRemote ejbTimerSessionBeanRemote;
    
    private SalesManagementModule salesManagementModule;
    private OperationsManagementModule operationsManagementModule;
    private CustomerServiceModule customerServiceModule;

    private Employee currentEmployee;

    public MainApp() {
    }

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
    }

    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote, RentalRateSessionBeanRemote rentalRateSessionBeanRemote, ModelSessionBeanRemote modelSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, OutletSessionBeanRemote outletSessionBeanRemote, TransitSessionBeanRemote transitSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote, EjbTimerSessionBeanRemote ejbTimerSessionBeanRemote) {
        this();
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.modelSessionBeanRemote = modelSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.outletSessionBeanRemote = outletSessionBeanRemote;
        this.transitSessionBeanRemote = transitSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.ejbTimerSessionBeanRemote = ejbTimerSessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to Merlion Car Rental Management ***\n");

            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;

            while (response < 1 || response > 2) {
                System.out.print("> ");

                response = scanner.nextInt();

                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        operationsManagementModule = new OperationsManagementModule(modelSessionBeanRemote, categorySessionBeanRemote, carSessionBeanRemote, outletSessionBeanRemote, transitSessionBeanRemote, employeeSessionBeanRemote, currentEmployee);
                        salesManagementModule = new SalesManagementModule(rentalRateSessionBeanRemote, categorySessionBeanRemote, currentEmployee);
                        
                        customerServiceModule = new CustomerServiceModule(reservationSessionBeanRemote, customerSessionBeanRemote,currentEmployee);
                        menuMain();
                        
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }

            if (response == 2) {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println("*** Merlion Car Rental Management :: Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if (username.length() > 0 && password.length() > 0) {
            currentEmployee = employeeSessionBeanRemote.employeeLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    private void menuMain() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion Car Rental Management ***\n");
            System.out.println("You are login as " + currentEmployee.getName() + " with " + currentEmployee.getRole().toString() + " rights\n");
            System.out.println("1: Sales Management");
            System.out.println("2: Operations Management");
            System.out.println("3: Customer Service");
            System.out.println("4: Allocate Cars To Current Day Reservations");
            System.out.println("5: Logout\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    try {
                        salesManagementModule.menuSalesManagement();
                    } catch (InvalidEmployeeRoleException ex) {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 2)
                {
                    try
                    {
                        operationsManagementModule.menuOperationsManagement();
                    }
                    catch (InvalidEmployeeRoleException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 3)
                {
                    try
                    {
                        customerServiceModule.menuCustomerService();
                    }
                    catch (InvalidEmployeeRoleException ex)
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 4)
                {
                    doAllocateCars();
                }
                else if (response == 5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 5)
            {
                break;
            }
        }
    }
    
    private void doAllocateCars() {
        try {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            
            System.out.println("*** Merlion Car Rental Management :: Allocate Cars To Current Day Reservations ***\n");
            System.out.print("Enter Date (dd/mm/yyyy hh:mm)> ");
            Date date = inputDateFormat.parse(scanner.nextLine().trim());
            ejbTimerSessionBeanRemote.allocateCarsToCurrentDayReservations(date);
            System.out.println("Allocation of cars to current day reservations on " + date + " done successfully!\n");
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (ReservationNotFoundException ex) {
            System.out.println("An unknown error has occurred while allocating cars to current day reservations!: " + ex.getMessage() + "\n");
        }
    }
}
