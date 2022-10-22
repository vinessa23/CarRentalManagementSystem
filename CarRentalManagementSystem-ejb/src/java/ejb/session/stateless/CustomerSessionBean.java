/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import java.util.List;
import util.exception.UnknownPersistenceException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.CustomerEmailExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author vinessa
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewCustomer(Customer customer) throws CustomerEmailExistException, UnknownPersistenceException{
        try {
            em.persist(customer);
            em.flush(); //only need to flush bcs we are returning the id!
            return customer.getCustomerId();
        } catch (PersistenceException ex){
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new CustomerEmailExistException("This email is already registered!");
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
    public List<Customer> retrieveAllCustomers() {
	Query query = em.createQuery("SELECT c FROM Customer c");
	List<Customer> customers = query.getResultList();
        //IF want to do lazy fetching
//	for(Customer c:customers) {
//	c.getRelatedEntities().size(); //for to many relationship
//	c.getRelatedEntity(); //for to one relationship
//	}
	return customers;
    }
    
    //dont need this, can check from the persistence exception msg 
    @Override
    public boolean emailExist(String email) {
        List<Customer> customers = retrieveAllCustomers();
        for(Customer c : customers) {
            if(email.equals(c.getEmail())) {
                return true;
            }
        }
        return false;
    }
    
    //retrieve by primary key ID
    @Override
    public Customer retrieveCustomerById(Long id) throws CustomerNotFoundException {
        Customer customer = em.find(Customer.class, id);
        if(customer != null) {
            return customer;
        } else {
            throw new CustomerNotFoundException("Customer ID " + id + " does not exist!");
        }
    }
    
    //retrieve by non-ID that must be unique
    @Override
    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM Customer c WHERE c.email = :inEmail");
        query.setParameter("inEmail", email);      
        try {
            return (Customer) query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CustomerNotFoundException("Customer email " + email + " does not exist!");
        }
    }
    
    @Override
    public Customer customerLogin(String email, String password) throws InvalidLoginCredentialException {
        try
        {
            Customer customer = retrieveCustomerByEmail(email);
            
            if(customer.getPassword().equals(password))
            {
                //TODO: remember to lazy fetch here if any               
                return customer;
            }
            else
            {
                throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
            }
        }
        catch(CustomerNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
        }
    }
    
}
