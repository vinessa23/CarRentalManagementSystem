/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CustomerEmailExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Remote
public interface CustomerSessionBeanRemote {
    public Long createNewCustomer(Customer customer) throws CustomerEmailExistException, UnknownPersistenceException, InputDataValidationException;

    public List<Customer> retrieveAllCustomers();

    public boolean emailExist(String email);

    public Customer retrieveCustomerById(Long id) throws CustomerNotFoundException;

    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException;

    public Customer customerLogin(String email, String password) throws InvalidLoginCredentialException;
}
