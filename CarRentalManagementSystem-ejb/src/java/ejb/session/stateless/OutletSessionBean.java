/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Outlet;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.OutletNameExistException;
import util.exception.OutletNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Stateless
public class OutletSessionBean implements OutletSessionBeanRemote, OutletSessionBeanLocal {

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createNewOutlet(Outlet outlet) throws OutletNameExistException, UnknownPersistenceException {
        try {
            em.persist(outlet);
            em.flush();
            return outlet.getOutletId();
        } catch (PersistenceException ex){
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new OutletNameExistException();
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
    public List<Outlet> retrieveAllOutlets() {
	Query query = em.createQuery("SELECT o FROM Outlet o");
	List<Outlet> outlets = query.getResultList();
	return outlets;
    }
    
    @Override
    public Outlet retrieveOutletById(Long id) throws OutletNotFoundException {
        Outlet outlet = em.find(Outlet.class, id);
        if(outlet != null) {
            return outlet;
        } else {
            throw new OutletNotFoundException("Outlet ID " + id + " does not exist!");
        }
    }
    
    @Override
    public Outlet retrieveOutletByName(String name) throws OutletNotFoundException {
        Query query = em.createQuery("SELECT o FROM Outlet o WHERE o.name = :inName");
        query.setParameter("inName", name);      
        try {
            return (Outlet) query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex)
        {
            throw new OutletNotFoundException("Outlet name " + name + " does not exist!");
        }
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
