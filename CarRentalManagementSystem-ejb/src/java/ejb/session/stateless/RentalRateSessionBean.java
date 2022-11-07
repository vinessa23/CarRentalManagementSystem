/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.RentalRate;
import entity.Reservation;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
    public Long createNewRentalRate(RentalRate rentalRate, Long categoryId) throws UnknownPersistenceException{
        try {
            Category category = em.find(Category.class, categoryId);
            em.persist(rentalRate);
            category.getRentalRates().add(rentalRate);
            em.flush(); //only need to flush bcs we are returning the id!
            return rentalRate.getRentalRateId();
        } catch (PersistenceException ex){
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }
    
    
    
    @Override
    public List<RentalRate> retrieveAllRentalRates() throws RentalRateNotFoundException{
        //TODO: sort by category first then validity period, not sure whether JPQL can sort by date automatically
        //??? really not sure how to sort this, how to sort the dates if not all rental rates hv dates
	Query query = em.createQuery("SELECT r FROM Category c JOIN c.rentalRates r ORDER BY c.categoryName");
        try {
            return query.getResultList();
        } catch (NoResultException ex) {
            throw new RentalRateNotFoundException("No rental rates have been recorded");
        }
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
        if(!isRentalRateUsed(rentalRateToRemove)) {
            em.remove(rentalRateToRemove);
        } else {
            rentalRateToRemove.setEnabled(Boolean.FALSE);
        }
    }
    
    private boolean isRentalRateUsed(RentalRate rentalRate) {
        try {
            Query query = em.createQuery("SELECT re FROM Reservation re");
            List<Reservation> reservations = query.getResultList();
            for (Reservation r : reservations) {
                List<RentalRate> rates = r.getRentalRates();
                for(RentalRate rate: rates) {
                    if(rate.getRentalRateId() == rentalRate.getRentalRateId()) {
                        return true;
                    }
                }
            }
            return false;
        } catch (NoResultException ex) { //no reservations yet
            return false;
        }
       
    }

}
