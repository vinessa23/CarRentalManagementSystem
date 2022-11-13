/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsmanagementclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.EjbTimerSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.TransitSessionBeanRemote;
import entity.Car;
import entity.Category;
import entity.Employee;
import entity.Model;
import entity.Outlet;
import entity.Reservation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.EmployeeRoles;
import util.exception.CarLicensePlateExistException;
import util.exception.CarNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.EmployeeFromDifferentOutletException;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidEmployeeRoleException;
import util.exception.ModelIsNotEnabledException;
import util.exception.ModelNameExistException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
public class OperationsManagementModule {
    
    private ModelSessionBeanRemote modelSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private CarSessionBeanRemote carSessionBeanRemote;
    private OutletSessionBeanRemote outletSessionBeanRemote;
    private TransitSessionBeanRemote transitSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private Employee currentEmployee;

    public OperationsManagementModule() {
    }

    public OperationsManagementModule(ModelSessionBeanRemote modelSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, CarSessionBeanRemote carSessionBeanRemote, OutletSessionBeanRemote outletSessionBeanRemote, TransitSessionBeanRemote transitSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, Employee currentEmployee) {
        this();
        this.modelSessionBeanRemote = modelSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.carSessionBeanRemote = carSessionBeanRemote;
        this.outletSessionBeanRemote = outletSessionBeanRemote;
        this.transitSessionBeanRemote = transitSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuOperationsManagement() throws InvalidEmployeeRoleException {
        
        if(currentEmployee.getRole()!= EmployeeRoles.OPERATIONS)
        {
            throw new InvalidEmployeeRoleException("You don't have OPERATIONS rights to access the operations management module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion Car Rental Management :: Operations Management ***\n");
            System.out.println("1: Create New Model");
            System.out.println("2: View All Models");
            System.out.println("3: Update Model");
            System.out.println("4: Delete Model");
            System.out.println("-----------------------");
            System.out.println("5: Create New Car");
            System.out.println("6: View Car Details");
            System.out.println("7: View All Cars");
            System.out.println("-----------------------");
            System.out.println("8: View Transit Drive Dispatch Records for Current Day Reservations");
            System.out.println("9: Assign Transit Driver");
            System.out.println("10: Update Transit As Completed");
            System.out.println("-----------------------");
            System.out.println("11: Back\n");
            response = 0;
            
            while(response < 1 || response > 11)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateNewModel();
                }
                else if(response == 2)
                {
                    doViewAllModels();
                }
                else if(response == 3)
                {
                    doUpdateModel();
                }
                else if(response == 4)
                {
                    doDeleteModel();
                }
                else if(response == 5)
                {
                    doCreateNewCar();
                }
                else if(response == 6)
                {
                    doViewCarDetails();
                }
                else if(response == 7)
                {
                    doViewAllCars();
                }
                else if(response == 8)
                {
                    doViewTransitDriverDispatchRecordsForCurrentOutlet();
                }
                else if(response == 9)
                {
                    doAssignTransitDriver();
                }
                else if(response == 10)
                {
                    doUpdateTransitAsCompleted();
                }
                else if (response == 11)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 11)
            {
                break;
            }
        }
    }
    
    private void doCreateNewModel() {
        try {
            Scanner scanner = new Scanner(System.in);
            Model newModel = new Model();
            
            System.out.println("*** Merlion Car Rental Management :: Operations Management :: Create New Model ***\n");
            System.out.print("Enter Make Name> ");
            newModel.setMakeName(scanner.nextLine().trim());
            System.out.print("Enter Model Name> ");
            newModel.setModelName(scanner.nextLine().trim());
            System.out.print("Enter Category Name> ");
            String categoryName = scanner.nextLine().trim();
            Long categoryId = categorySessionBeanRemote.retrieveCategoryByName(categoryName).getCategoryId();
            Long newModelId = modelSessionBeanRemote.createNewModel(categoryId, newModel);
            System.out.println("New model created successfully!: " + newModelId + "\n");
            
        } catch (CategoryNotFoundException ex) {
            System.out.println("An error has occurred while creating the new model!: " + ex.getMessage() + "\n");
        } catch (ModelNameExistException ex) {
            System.out.println("An error has occurred while creating the new model!: " + ex.getMessage() + "\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An unknown error has occurred while creating the new model!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doViewAllModels() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Merlion Car Rental Management :: Operations Management :: View All Models ***\n");
        
        List<Model> models = modelSessionBeanRemote.retrieveAllModels();
        System.out.printf("%8s%20s%20s%20s\n", "Model ID", "Make Name", "Model Name", "Category Name");

        for(Model model : models)
        {
            System.out.printf("%8s%20s%20s%20s\n", model.getModelId().toString(), model.getMakeName(), model.getModelName(), model.getCategory().getCategoryName());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doUpdateModel() {
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            
            System.out.println("*** Merlion Car Rental Management :: Operations Management :: Update Model ***\n");
            System.out.print("Enter Make Name> ");
            String makeName = scanner.nextLine().trim();
            System.out.print("Enter Model Name> ");
            String modelName = scanner.nextLine().trim();
            
            Model model = modelSessionBeanRemote.retrieveModelByMakeModelName(makeName, modelName);
            
            System.out.print("Enter New Make Name (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                model.setMakeName(input);
            }
            
            System.out.print("Enter New Model Name (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                model.setModelName(input);
            }
            
            System.out.print("Enter New Category Name (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                Category newCategory = categorySessionBeanRemote.retrieveCategoryByName(input);
                model.setCategory(newCategory);
            }
            
            modelSessionBeanRemote.updateModel(model);
            System.out.println("Model updated successfully!\n");
        } catch (ModelNotFoundException ex) {
            System.out.println("An error has occurred while updating the model!: " + ex.getMessage() + "\n");
        } catch (CategoryNotFoundException ex) {
            System.out.println("An error has occurred while updating the model!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doDeleteModel() {
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            
            System.out.println("*** Merlion Car Rental Management :: Operations Management :: Delete Model ***\n");
            System.out.print("Enter Make Name> ");
            String makeName = scanner.nextLine().trim();
            System.out.print("Enter Model Name> ");
            String modelName = scanner.nextLine().trim();
            Model model = modelSessionBeanRemote.retrieveModelByMakeModelName(makeName,modelName);
            System.out.printf("Confirm Delete Model (Make Name: %s, ModelName: %s) (Enter 'Y' to Delete)> ", model.getMakeName(), model.getModelName());
            input = scanner.nextLine().trim();
            
            if(input.equals("Y"))
            {
                try {
                    modelSessionBeanRemote.deleteModel(model.getModelId());
                    System.out.println("Model deleted successfully!\n");
                } catch (ModelNotFoundException ex) {
                    System.out.println("An error has occurred while deleting the model!: " + ex.getMessage() + "\n");
                }
            }
            else
            {
                System.out.println("Model NOT deleted!\n");
            }
        } catch (ModelNotFoundException ex) {
            System.out.println("An error has occurred while deleting the model!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doCreateNewCar() {
        try {
            Scanner scanner = new Scanner(System.in);
            Car newCar = new Car();
            
            System.out.println("*** Merlion Car Rental Management :: Operations Management :: Create New Car ***\n");
            System.out.print("Enter License Plate> ");
            newCar.setLicensePlate(scanner.nextLine().trim());
            System.out.print("Enter Make Name> ");
            String makeName = scanner.nextLine().trim();
            System.out.print("Enter Model Name> ");
            String modelName = scanner.nextLine().trim();
            System.out.print("Enter Car Status> ");
            String carStatus = scanner.nextLine().trim();
            System.out.print("Enter Outlet Name> ");
            String outletName = scanner.nextLine().trim();
            
            Long modelId = modelSessionBeanRemote.retrieveModelByMakeModelName(makeName, modelName).getModelId();
            Long outletId = outletSessionBeanRemote.retrieveOutletByName(outletName).getOutletId();
            Long newCarId = carSessionBeanRemote.createNewCar(outletId, modelId, newCar); //car status is set to available by default?
            System.out.println("New car created successfully!: " + newCarId + "\n");
        } catch (ModelNotFoundException ex) {
            System.out.println("An error has occurred while creating the new car!: " + ex.getMessage() + "\n");
        } catch (OutletNotFoundException ex) {
            System.out.println("An error has occurred while creating the new car!: " + ex.getMessage() + "\n");
        } catch (ModelIsNotEnabledException ex) {
            System.out.println("An error has occurred while creating the new car!: " + ex.getMessage() + "\n");
        } catch (CarLicensePlateExistException ex) {
            System.out.println("An error has occurred while creating the new car!: " + ex.getMessage() + "\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An error has occurred while creating the new car!: " + ex.getMessage() + "\n");
        }
    }
        private void doViewAllCars() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Merlion Car Rental Management :: Operations Management :: View All Cars ***\n");
        
        List<Car> cars = carSessionBeanRemote.retrieveAllCars();
        System.out.printf("%8s%20s%20s%20s%20s%20s%20s\n", "Car ID", "License Plate", "Car Colour", "Car Status", "Make Name", "Model Name", "Outlet Name");

        for(Car car : cars)
        {
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s\n", car.getCarId(), car.getLicensePlate(), car.getColour(), car.getCarStatus().toString(), car.getModel().getMakeName(), car.getModel().getModelName(), car.getOutlet().getName());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
        
        private void doViewCarDetails() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            
            System.out.println("*** Merlion Car Rental Management :: Operations Management :: View Car Details ***\n");
            System.out.print("Enter License Plate> ");
            String licensePlate = scanner.nextLine().trim();
            Car car = carSessionBeanRemote.retrieveCarByLicensePlate(licensePlate);
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s\n", "Car ID", "License Plate", "Car Colour", "Car Status", "Make Name", "Model Name", "Outlet Name");
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s\n", car.getCarId(), car.getLicensePlate(), car.getColour(), car.getCarStatus().toString(), car.getModel().getMakeName(), car.getModel().getModelName(), car.getOutlet().getName());
            System.out.println("------------------------");
            System.out.println("1: Update Car");
            System.out.println("2: Delete Car");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                doUpdateCar(car);
            }
            else if(response == 2)
            {
                doDeleteCar(car);
            }
        } catch (CarNotFoundException ex) {
            System.out.println("An error has occurred while retrieving car!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doUpdateCar(Car car) {
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            String makeName;
            
            System.out.println("*** Merlion Car Rental Management :: Operations Management :: View Car Details :: Update Car ***\n");
            System.out.print("Enter New License Plate (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                car.setLicensePlate(input);
            }
            
            System.out.print("Enter New Colour (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                car.setColour(input);
            }
            
            System.out.print("Enter New Make Name (blank if no change)> ");
            makeName = scanner.nextLine().trim();
            
            System.out.print("Enter New Model Name (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(makeName.length() > 0 && input.length() > 0)
            {
                try {
                    Model newModel = modelSessionBeanRemote.retrieveModelByMakeModelName(makeName, input);
                    car.setModel(newModel);
                } catch (ModelNotFoundException ex) {
                    System.out.println("An error has occurred while updating car!: " + ex.getMessage() + "\n");
                }
            }
            
            System.out.print("Enter New Outlet Name (blank if no change)> ");
            input = scanner.nextLine().trim();
            if(input.length() > 0)
            {
                try {
                    Outlet newOutlet = outletSessionBeanRemote.retrieveOutletByName(input);
                    car.setOutlet(newOutlet);
                } catch (OutletNotFoundException ex) {
                    System.out.println("An error has occurred while updating car!: " + ex.getMessage() + "\n");
                }
            }
            carSessionBeanRemote.updateCar(car);
            System.out.println("Car updated successfully!\n");
        } catch (CarNotFoundException ex) {
            System.out.println("An error has occurred while updating car!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doDeleteCar(Car car) {
        Scanner scanner = new Scanner(System.in);     
        String input;
        
        System.out.println("*** Merlion Car Rental Management :: Operations Management :: View Car Details :: Delete Car ***\n");
        System.out.printf("Confirm Delete Car (License Plate: %s) (Enter 'Y' to Delete)> ", car.getLicensePlate());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try {
                carSessionBeanRemote.deleteCar(car.getCarId());
                System.out.println("Car deleted successfully!\n");
            } catch (CarNotFoundException ex) {
                System.out.println("An error has occurred while deleting car!: " + ex.getMessage() + "\n");
            }
        }
        else
        {
            System.out.println("Car NOT deleted!\n");
        }
    }
    
    private void doViewTransitDriverDispatchRecords() {
        try {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            System.out.println("*** Merlion Car Rental Management :: Operations Management :: View Transit Driver Dispatch Records For Current Day Reservations ***\n");
            System.out.println("Transit Driver Dispatch Record for " + LocalTime.now() + "\n");
            List<Reservation> reservations = transitSessionBeanRemote.getTransitRecordsForToday(currentEmployee.getEmployeeId());
            System.out.printf("%15s%20s%20s%20s%20s%20s%20s\n", "Seq No.", "Reservation ID", "Booking Status", "Pickup Date", "Return Date", "Pickup Outlet", "Return Outlet");
            int i = 1;
            for(Reservation reservation:reservations)
            {
                System.out.printf("%15s%20s%20s%20s%20s%20s%20s\n", i, reservation.getReservationId(), reservation.getBookingStatus(), outputDateFormat.format(reservation.getStartDate()), outputDateFormat.format(reservation.getEndDate()), reservation.getPickupOutlet().getName(), reservation.getReturnOutlet().getName());
                i++;
            }
            
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (EmployeeNotFoundException ex) {
            System.out.println("An error has occurred while viewing the transit dispatch driver records!: " + ex.getMessage() + "\n");
        }
    }
//    For testing: manually key in date (for current day)
    private void doViewTransitDriverDispatchRecordsForCurrentOutlet() {
        try {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            System.out.println("*** Merlion Car Rental Management :: Operations Management :: View Transit Driver Dispatch Records For Current Day Reservations ***\n");
            System.out.print("Enter Date (dd/mm/yyyy hh:mm)> ");
            Date date = outputDateFormat.parse(scanner.nextLine().trim());
            System.out.println("Transit Driver Dispatch Record for " + date + "\n");
            List<Reservation> reservations = transitSessionBeanRemote.getTransitRecordsForToday(currentEmployee.getEmployeeId(), date);
            System.out.printf("%15s%20s%20s%20s%20s%20s%20s\n", "Seq No.", "Reservation ID", "Booking Status", "Pickup Date", "Return Date", "Pickup Outlet", "Return Outlet");
            int i = 1;
            for(Reservation reservation:reservations)
            {
                System.out.printf("%15s%20s%20s%20s%20s%20s%20s\n", i, reservation.getReservationId(), reservation.getBookingStatus(), outputDateFormat.format(reservation.getStartDate()), outputDateFormat.format(reservation.getEndDate()), reservation.getPickupOutlet().getName(), reservation.getReturnOutlet().getName());
                i++;
            }
            
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (EmployeeNotFoundException ex) {
            System.out.println("An error has occurred while viewing the transit dispatch driver records!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doAssignTransitDriver() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** CarMS Management Client :: Sales Management :: Do Assign Transit Driver ***\n");
            System.out.print("Enter Reservation Id> ");
            Long id = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Enter Employee (Transit Driver) Name> ");
            String name = scanner.nextLine().trim();
            
            Employee employee = employeeSessionBeanRemote.retrieveEmployeeByUsername(name);
            
            transitSessionBeanRemote.assignTransitDriver(id, employee.getEmployeeId());
            System.out.println("Succesfully assigned transit driver!");
        } catch (EmployeeNotFoundException ex) {
            System.out.println("An error has occurred while assigning transit driver!: " + ex.getMessage() + "\n");
        } catch (EmployeeFromDifferentOutletException ex) {
            System.out.println("An error has occurred while assigning transit driver!: " + ex.getMessage() + "\n");
        } catch (ReservationNotFoundException ex) {
            System.out.println("An error has occurred while assigning transit driver!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doUpdateTransitAsCompleted() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** CarMS Management Client :: Sales Management :: Update Transit As Completed ***\n");
            System.out.print("Enter Reservation Id> ");
            Long id = scanner.nextLong();
            scanner.nextLine();
            
            transitSessionBeanRemote.updateTransitRecordComplete(id);
            System.out.println("Succesfully updated transit as completed!");
        } catch (ReservationNotFoundException ex) {
            System.out.println("An error has occurred while updating transit record!: " + ex.getMessage() + "\n");
        }
        
    }
}
