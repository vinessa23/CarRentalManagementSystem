/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import java.util.List;
import javax.ejb.Local;
import util.exception.CustomerEmailExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Local
public interface CustomerSessionBeanLocal {

    public Long createNewCustomer(Customer customer) throws CustomerEmailExistException, UnknownPersistenceException;

    public List<Customer> retrieveAllCustomer();

    public boolean emailExist(String email);

    public Customer retrieveCustomerById(Long id) throws CustomerNotFoundException;

    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException;

    public Customer customerLogin(String email, String password) throws InvalidLoginCredentialException;
    
}
