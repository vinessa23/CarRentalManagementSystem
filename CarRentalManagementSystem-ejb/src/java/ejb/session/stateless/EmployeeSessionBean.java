/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewEmployee(Employee employee) throws EmployeeUsernameExistException, UnknownPersistenceException{
        try {
            em.persist(employee);
            em.flush(); //only need to flush bcs we are returning the id!
            return employee.getEmployeeId();
        } catch (PersistenceException ex){
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new EmployeeUsernameExistException("This username is already registered!");
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public List<Employee> retrieveAllEmployees() {
	Query query = em.createQuery("SELECT e FROM Employee e");
	List<Employee> employees = query.getResultList();
        //IF want to do lazy fetching
//	for(Employee c:employees) {
//	c.getRelatedEntities().size(); //for to many relationship
//	c.getRelatedEntity(); //for to one relationship
//	}
	return employees;
    }
    
    //retrieve by primary key ID
    @Override
    public Employee retrieveEmployeeById(Long id) throws EmployeeNotFoundException {
        Employee employee = em.find(Employee.class, id);
        if(employee != null) {
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee ID " + id + " does not exist!");
        }
    }
    
    //retrieve by non-ID that must be unique
    @Override
    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);      
        try {
            return (Employee) query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex)
        {
            throw new EmployeeNotFoundException("Employee username " + username + " does not exist!");
        }
    }
    
    @Override
    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException {
        try
        {
            Employee employee = retrieveEmployeeByUsername(username);
            
            if(employee.getPassword().equals(password))
            {
                //TODO: remember to lazy fetch here if any               
                return employee;
            }
            else
            {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        }
        catch(EmployeeNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    

}

