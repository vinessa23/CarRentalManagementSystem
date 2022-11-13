/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsmanagementclient;

import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import entity.Employee;
import entity.RentalRate;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.EmployeeRoles;
import util.enumeration.RentalRateType;
import util.exception.CategoryNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidEmployeeRoleException;
import util.exception.RentalRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
public class SalesManagementModule {

    private RentalRateSessionBeanRemote rentalRateSessionBeanRemote;
    private CategorySessionBeanRemote categorySessionBeanRemote;
    private Employee currentEmployee;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public SalesManagementModule()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public SalesManagementModule(RentalRateSessionBeanRemote rentalRateSessionBeanRemote, CategorySessionBeanRemote categorySessionBeanRemote, Employee currentEmployee) {
        this();
        this.rentalRateSessionBeanRemote = rentalRateSessionBeanRemote;
        this.categorySessionBeanRemote = categorySessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    public void menuSalesManagement() throws InvalidEmployeeRoleException {
        
        if(currentEmployee.getRole()!= EmployeeRoles.SALES)
        {
            throw new InvalidEmployeeRoleException("You don't have SALES rights to access the sales management module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Merlion Car Rental Management :: Sales Management ***\n");
            System.out.println("1: Create Rental Rate");
            System.out.println("2: View All Rental Rates");
            System.out.println("3: View Rental Rate Details");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doCreateRentalRate();
                }
                else if(response == 2)
                {
                    doViewAllRentalRates();
                }
                else if(response == 3)
                {
                    doViewRentalRateDetails();
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 4)
            {
                break;
            }
        }
    }
    
    private void doCreateRentalRate() {
        try {
            Scanner scanner = new Scanner(System.in);
            RentalRate newRentalRate = new RentalRate();
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            
            System.out.println("*** Merlion Car Rental Management :: Sales Management :: Create Rental Rate ***\n");
            System.out.print("Enter Rental Rate Name> ");
            newRentalRate.setName(scanner.nextLine().trim());
            System.out.print("Enter Rental Rate Type> ");
            String type = scanner.nextLine().trim();
            if (type.equals("Peak")) {
                newRentalRate.setType(RentalRateType.PEAK);
            }
            System.out.print("Enter Category Name> ");
            String categoryName = scanner.nextLine().trim();
            System.out.print("Enter Rate Per Day> ");
            newRentalRate.setRatePerDay(scanner.nextBigDecimal());
            scanner.nextLine();
            System.out.print("Enter validity period? (Enter 'Y' to set validity period> ");
            String input = scanner.nextLine().trim();
            if (input.equals("Y")) {
                System.out.print("Enter Start Date (dd/mm/yyyy hh:mm)> ");
                newRentalRate.setStartDate(inputDateFormat.parse(scanner.nextLine().trim()));
                System.out.print("Enter End Date (dd/mm/yyyy hh:mm)> ");
                newRentalRate.setEndDate(inputDateFormat.parse(scanner.nextLine().trim()));
            }
 
            Long categoryId = categorySessionBeanRemote.retrieveCategoryByName(categoryName).getCategoryId();
            Long newRentalRateId = rentalRateSessionBeanRemote.createNewRentalRate(newRentalRate, categoryId);
            System.out.println("New rental rate created successfully!: " + newRentalRateId + "\n");
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (CategoryNotFoundException ex) {
            System.out.println("An error has occurred while creating the new model!: " + ex.getMessage() + "\n");
        } catch (UnknownPersistenceException ex) {
            System.out.println("An error has occurred while creating the new model!: " + ex.getMessage() + "\n");
        } catch (InputDataValidationException ex) {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private void doViewAllRentalRates() {
        try {
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("*** Merlion Car Rental Management :: Sales Management :: View All Rental Rates ***\n");
            
            List<RentalRate> rentalRates = rentalRateSessionBeanRemote.retrieveAllRentalRates();
            System.out.printf("%8s%30s%25s%20s%20s%35s%35s\n", "Rental Rate ID", "Rental Rate Name", "Rental Rate Type", "Category Name", "Rate Per Day", "Start Date", "End Date");
            
            for(RentalRate rentalRate : rentalRates)
            {
                System.out.printf("%8s%40s%20s%20s%20s%35s%35s\n", rentalRate.getRentalRateId().toString(), rentalRate.getName(), rentalRate.getType().toString(), rentalRate.getCategory().getCategoryName(), rentalRate.getRatePerDay(), rentalRate.getStartDate(), rentalRate.getEndDate());
            }
            
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        } catch (RentalRateNotFoundException ex) {
            System.out.println("An error has occurred while retrieving all rental rates!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doViewRentalRateDetails() {
        try {
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            
            System.out.println("*** Merlion Car Rental Management :: Sales Management :: View Rental Rate Details ***\n");
            System.out.print("Enter Rental Rate Name> ");
            String name = scanner.nextLine().trim();
            RentalRate rentalRate = rentalRateSessionBeanRemote.retrieveRentalRateByName(name);
            System.out.printf("%8s%30s%25s%20s%20s%35s%35s\n", "Rental Rate ID", "Rental Rate Name", "Rental Rate Type", "Category Name", "Rate Per Day", "Start Date", "End Date");
            System.out.printf("%8s%40s%20s%20s%20s%35s%35s\n", rentalRate.getRentalRateId().toString(), rentalRate.getName(), rentalRate.getType().toString(), rentalRate.getCategory().getCategoryName(), rentalRate.getRatePerDay(), rentalRate.getStartDate(), rentalRate.getEndDate());
            System.out.println("------------------------");
            System.out.println("1: Update Rental Rate");
            System.out.println("2: Delete Rental Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                doUpdateRentalRate(rentalRate);
            }
            else if(response == 2)
            {
                doDeleteRentalRate(rentalRate);
            }
        } catch (RentalRateNotFoundException ex) {
            System.out.println("An error has occurred while retrieving rental rate!: " + ex.getMessage() + "\n");
        }
    }
    
    private void doUpdateRentalRate(RentalRate rentalRate) {
        Scanner scanner = new Scanner(System.in);
        String input;
        String makeName;
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        System.out.println("*** Merlion Car Rental Management :: Sales Management :: View Rental Rate Details :: Update Rental Rate ***\n");

        System.out.print("Enter New Rate Per Day (blank if no change)> ");
        BigDecimal newRate = scanner.nextBigDecimal();
        scanner.nextLine();
        if(newRate != null)
        {
            rentalRate.setRatePerDay(newRate);
        }
        
        System.out.print("Change Validity Period? (Enter 'Y' to set validity period)> ");
        input = scanner.nextLine().trim();
            if (input.equals("Y")) {
                System.out.print("Enter New Start Date (dd/mm/yyyy hh:mm)> ");
                try {
                    Date startDate = inputDateFormat.parse(scanner.nextLine().trim());
                    if(newRate != null)
                    {
                        rentalRate.setStartDate(startDate);
                    }

                    System.out.print("Enter New End Date (dd/mm/yyyy hh:mm)> ");
                    Date endDate = outputDateFormat.parse(scanner.nextLine().trim());
                    if(newRate != null)
                    {
                        rentalRate.setEndDate(endDate);
                    }
                }
                catch (ParseException ex) {
                    System.out.println("Invalid date input!\n");
                }
            }
                
        Set<ConstraintViolation<RentalRate>>constraintViolations = validator.validate(rentalRate);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                rentalRateSessionBeanRemote.updateRentalRate(rentalRate);
                System.out.println("Car updated successfully!\n");
            }
            catch (RentalRateNotFoundException ex) {
                System.out.println("An error has occurred while updating rental rate!: " + ex.getMessage() + "\n");
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForRentalRate(constraintViolations);
        }
    }
        
    private void doDeleteRentalRate(RentalRate rentalRate) {
        Scanner scanner = new Scanner(System.in);     
        String input;
        
        System.out.println("*** Merlion Car Rental Management :: Sales Management :: View Rental Rate Details :: Delete Rental Rate ***\n");
        System.out.printf("Confirm Delete Rental Rate %s (Enter 'Y' to Delete)> ", rentalRate.getName());
        input = scanner.nextLine().trim();
        
        if(input.equals("Y"))
        {
            try {
                rentalRateSessionBeanRemote.deleteRentalRate(rentalRate.getRentalRateId());
                System.out.println("Rental Rate deleted successfully!\n");
            } catch (RentalRateNotFoundException ex) {
                System.out.println("An error has occurred while deleting rental rate!: " + ex.getMessage() + "\n");
            }

        }
        else
        {
            System.out.println("Rental Rate NOT deleted!\n");
        }
    }
    
    private void showInputDataValidationErrorsForRentalRate(Set<ConstraintViolation<RentalRate>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
