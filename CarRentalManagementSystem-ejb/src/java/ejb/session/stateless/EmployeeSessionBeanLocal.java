/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Local
public interface EmployeeSessionBeanLocal {

    public Long createNewEmployee(Employee employee) throws EmployeeUsernameExistException, UnknownPersistenceException;

    public List<Employee> retrieveAllEmployees();

    public Employee retrieveEmployeeById(Long id) throws EmployeeNotFoundException;

    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;

    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException;
    
}
