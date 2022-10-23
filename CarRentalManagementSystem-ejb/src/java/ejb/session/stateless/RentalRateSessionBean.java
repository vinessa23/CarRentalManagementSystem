/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalRate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.RentalRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Stateless
public class RentalRateSessionBean implements RentalRateSessionBeanRemote, RentalRateSessionBeanLocal {

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewRentalRate(RentalRate rentalRate) throws UnknownPersistenceException{
        try {
            em.persist(rentalRate);
            em.flush(); //only need to flush bcs we are returning the id!
            return rentalRate.getRentalRateId();
        } catch (PersistenceException ex){
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }
    
    @Override
    public List<RentalRate> retrieveAllRentalRates() {
        //TODO: sort by category first then validity period, not sure whether JPQL can sort by date automatically
	Query query = em.createQuery("SELECT r FROM RentalRate r ORDER BY r.startDate");
	List<RentalRate> rentalRates = query.getResultList();
        //IF want to do lazy fetching
//	for(RentalRate c:rentalRates) {
//	c.getRelatedEntities().size(); //for to many relationship
//	c.getRelatedEntity(); //for to one relationship
//	}
	return rentalRates;
    }

    //retrieve by primary key ID
    @Override
    public RentalRate retrieveRentalRateById(Long id) throws RentalRateNotFoundException {
        RentalRate rentalRate = em.find(RentalRate.class, id);
        if(rentalRate != null) {
            return rentalRate;
        } else {
            throw new RentalRateNotFoundException("RentalRate ID " + id + " does not exist!");
        }
    }
    
    @Override
    public void updateRentalRate(RentalRate rentalRate) throws RentalRateNotFoundException
    {
        if(rentalRate != null && rentalRate.getRentalRateId()!= null)
        {
            RentalRate rentalRateToUpdate = retrieveRentalRateById(rentalRate.getRentalRateId());

            rentalRateToUpdate.setRatePerDay(rentalRate.getRatePerDay());
            rentalRateToUpdate.setStartDate(rentalRate.getStartDate());
            rentalRateToUpdate.setEndDate(rentalRate.getEndDate());
            rentalRateToUpdate.setDaysOfWeek(rentalRate.getDaysOfWeek());
            rentalRateToUpdate.setEnabled(rentalRate.getEnabled());
        }
        else
        {
            throw new RentalRateNotFoundException("Rental rate ID not provided for rentalRate to be updated");
        }
    }
    
    @Override
    public void deleteRentalRate(Long rentalRateId) throws RentalRateNotFoundException
    {
        RentalRate rentalRateToRemove = retrieveRentalRateById(rentalRateId);
        
        //retrieve the associated entity here 
        //List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities = saleTransactionEntitySessionBeanLocal.retrieveSaleTransactionLineItemsByRentalRateId(rentalRateId);
        
        //if(saleTransactionLineItemEntities.isEmpty())
        //{
            em.remove(rentalRateToRemove);
        //}
        //else
        //{
        //    rentalRateToRemove.setEnabled(false);
        //}
    }
}
