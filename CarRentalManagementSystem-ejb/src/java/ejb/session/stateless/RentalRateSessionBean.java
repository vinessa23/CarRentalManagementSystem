/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.RentalRate;
import entity.Reservation;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.comparator.RentalRateComparator;
import util.enumeration.RentalRateType;
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
            rentalRate.setCategory(category);
            em.persist(rentalRate);
            category.getRentalRates().add(rentalRate);
            em.flush(); //only need to flush bcs we are returning the id!
            return rentalRate.getRentalRateId();
        } catch (PersistenceException ex){
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }
    
    
    //only retrieve the enabled rental rates
    @Override
    public List<RentalRate> retrieveAllRentalRates() throws RentalRateNotFoundException{
	Query query = em.createQuery("SELECT r FROM RentalRate r");
        try {
            List<RentalRate> rr = query.getResultList();
            List<RentalRate> res = new ArrayList<>();
            for(RentalRate r : rr) {
                if(r.getEnabled()) {
                    r.getReservations().size();
                    res.add(r);
                }
            }
            res.sort(new RentalRateComparator());
            return res;
        } catch (NoResultException ex) {
            throw new RentalRateNotFoundException("No rental rate found");
        }
    }
    
    private List<RentalRate> retrieveRentalRatesByCategory(Category category) throws RentalRateNotFoundException {
        try {
            List<RentalRate> all = retrieveAllRentalRates();
            List<RentalRate> rr = new ArrayList<>();

            for(RentalRate r: all) {
                if(r.getCategory().getCategoryId() == category.getCategoryId()) {
                    r.getReservations().size();
                    rr.add(r);
                }
            }

            return rr;
        } catch (RentalRateNotFoundException ex) {
            throw new RentalRateNotFoundException("No rental rate found");
        }
    }
    
    private RentalRate retrieveLowestValidRentalRate(Category category, Date starting) throws RentalRateNotFoundException {
        try {
            List<RentalRate> valid = retrieveValidRentalRate(category, starting);
            boolean hasNonDefault = false;
            List<RentalRate> considered = new ArrayList<>();
            
            for(RentalRate v : valid) {
                if (v.getType() == RentalRateType.PEAK || v.getType() == RentalRateType.PROMOTION) {
                    hasNonDefault = true;
                }
            }
            
            if(hasNonDefault) {
                for(RentalRate v : valid) {
                    if(v.getType() == RentalRateType.PEAK || v.getType() == RentalRateType.PROMOTION) {
                        considered.add(v);
                    }
                }
            } else {
                considered = valid;
            }
            if(considered.size() > 0) {
                RentalRate lowest = considered.get(0);
                for(RentalRate rental : considered) {
                    if (rental.getRatePerDay().compareTo(lowest.getRatePerDay()) < 0) {
                        lowest = rental;
                    } 
                } 
                return lowest;
            } else {
                throw new RentalRateNotFoundException("No rental rate found");
            }
        } catch (RentalRateNotFoundException ex) {
            throw new RentalRateNotFoundException("No rental rate found");
        }
    }
    
    private List<RentalRate> retrieveValidRentalRate(Category category, Date starting) throws RentalRateNotFoundException {
        try { 
            List<RentalRate> rr = retrieveRentalRatesByCategory(category);
            List<RentalRate> valid = new ArrayList<>();
            for(RentalRate r : rr) {
                if(r.getEnabled() == true && r.getType() == RentalRateType.DEFAULT) { //for default no starting date
                    valid.add(r);
                } else if(r.getEnabled() == true && starting.after(r.getStartDate()) && starting.before(r.getEndDate())) {
                    valid.add(r);
                } else {
                    //for testing
                    System.out.println("Not valid:" + r.getEnabled().toString() + " " + starting.toString() + " is outside " + r.getStartDate().toString() + " - " + r.getEndDate().toString());
                }
            }
            return valid;  
        } catch (RentalRateNotFoundException ex) {
            throw new RentalRateNotFoundException("No rental rate found");
        }
    }

    //retrieve by primary key ID
    @Override
    public RentalRate retrieveRentalRateById(Long id) throws RentalRateNotFoundException {
        RentalRate rentalRate = em.find(RentalRate.class, id);
        if(rentalRate != null) {
            rentalRate.getReservations().size();
            return rentalRate;
        } else {
            throw new RentalRateNotFoundException("RentalRate ID " + id + " does not exist!");
        }
    }
    
    @Override
    public RentalRate retrieveRentalRateByName(String name) throws RentalRateNotFoundException {
        Query query = em.createQuery("SELECT rr FROM RentalRate rr WHERE rr.name = :inName");
        query.setParameter("inName", name);      
        try {
            RentalRate rentalRate = (RentalRate) query.getSingleResult();
            rentalRate.getReservations().size();
            return rentalRate;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RentalRateNotFoundException("No rental rate found");
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
    
    @Override
    public List<RentalRate> calculateRentalRate(Category category, Date startingDate, Date endDate) throws RentalRateNotFoundException {
        try { 
            List<RentalRate> rr = retrieveRentalRatesByCategory(category);
            //assumption: as long as the starting date of the reservation is within the validity period then rr can be used
            List<RentalRate> res = new ArrayList<>();
            Date date = startingDate;
            while(date.before(endDate)) {
                RentalRate lowest = retrieveLowestValidRentalRate(category, date);
                res.add(lowest);
                date = nextDate(date);
            }
            return res;
        } catch (RentalRateNotFoundException ex) {
            throw new RentalRateNotFoundException("Rental rate ID not provided for rentalRate to be updated");
        }  
    }
    
    private Date nextDate(Date date) {
        LocalDateTime initialLDT = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime afterLDT = initialLDT.plusHours(24);
        return java.sql.Timestamp.valueOf(afterLDT);
    }

}
