/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Category;
import entity.Outlet;
import entity.Reservation;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.CategoryNameExistException;
import util.exception.CategoryNotFoundException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Stateless
public class CategorySessionBean implements CategorySessionBeanRemote, CategorySessionBeanLocal {

    @EJB(name = "OutletSessionBeanLocal")
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;
    
    
    
    @Override
    public Long createNewCategory(Category category) throws CategoryNameExistException, UnknownPersistenceException {
        try {
            em.persist(category);
            em.flush();
            return category.getCategoryId();
        } catch (PersistenceException ex){
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new CategoryNameExistException();
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
    public List<Category> retrieveAllCategories() {
	Query query = em.createQuery("SELECT c FROM Category c");
	List<Category> categories = query.getResultList();
	return categories;
    }
    
    @Override
    public Category retrieveCategoryById(Long id) throws CategoryNotFoundException {
        Category category = em.find(Category.class, id);
        if(category != null) {
            return category;
        } else {
            throw new CategoryNotFoundException("Category ID " + id + " does not exist!");
        }
    }
    
    @Override
    public Category retrieveCategoryByName(String categoryName) throws CategoryNotFoundException {
        Query query = em.createQuery("SELECT c FROM Category c WHERE c.categoryName = :inName");
        query.setParameter("inName", categoryName);      
        try {
            return (Category) query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CategoryNotFoundException("Category name " + categoryName + " does not exist!");
        }
    }
    
    private int numCarsForCategoryAndOutlet(Category category, Outlet outlet) {
        List<Car> cars = outlet.getCars();
        int result = 0;
        for(Car car : cars) {
            if(car.getModel().getCategory().getCategoryId() == category.getCategoryId()) {
                result++;
            }
        }
        return result;
    }
    
    private boolean isCategoryAvailableForThisPeriod(Outlet outlet, Category category, Date start, Date end) {
        if(isCategoryAvailableForThisPeriodOutlet(outlet, category, start, end, true)) {
            return true;
        } else {
            List<Outlet> outlets = outletSessionBeanLocal.retrieveAllOutlets();
            for(Outlet o : outlets) {
                if(!o.equals(outlet)) {
                    if(isCategoryAvailableForThisPeriodOutlet(o, category, start, end, false)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    private boolean isCategoryAvailableForThisPeriodOutlet(Outlet outlet, Category category, Date start, Date end, boolean sameOutlet) {
        int carAvailable = numCarsForCategoryAndOutlet(category, outlet);
        int carReserved = numOverlappingReservations(outlet, category, start, end, sameOutlet);
        if (carAvailable > carReserved) {
            return true;
        } else {
            return false;
        }
    }
    
    private int numOverlappingReservations(Outlet outlet, Category category, Date start, Date end, boolean sameOutlet) {
        try {
            List<Reservation> reservationThisOutlet = retrieveReservationsOutlet(outlet);
            int res = 0;
            for(Reservation r : reservationThisOutlet) {
                if(sameOutlet) {
                    if(start.before(r.getEndDate()) || end.after(r.getStartDate())) {
                        res++;
                    }
                } else {
                    if(start.before(plusHours(r.getEndDate(), 2)) || end.after(plusHours(r.getStartDate(), -2))) {
                        res++;
                    }
                }
            }
            return res;
        } catch (ReservationNotFoundException ex) { //if no reservation then no overlapping reservations
            return 0;
        }  
    }
    
    private List<Reservation> retrieveAllReservations() throws ReservationNotFoundException {
        Query query = em.createQuery("SELECT r FROM Reservation r");
        try {
            List<Reservation> reservations = query.getResultList();
            return reservations;
        } catch (NoResultException ex) {
            throw new ReservationNotFoundException("No reservations");
        }
    }
    
    private List<Reservation> retrieveReservationsOutlet(Outlet outlet) throws ReservationNotFoundException{
        try {
            List<Reservation> all = retrieveAllReservations();
            List<Reservation> res = new ArrayList<>();
            for(Reservation r : all) {
                if(r.getPickupOutlet().getOutletId() == outlet.getOutletId()) {
                    res.add(r);
                }
            }
            return res;
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException("No reservations");
        }
    }

    public List<Category> categoriesAvailableForThisPeriod(Outlet outlet, Date start, Date end) {
        List<Category> all = retrieveAllCategories();
        List<Category> res = new ArrayList<>();
        for(Category c : all) {
            if(isCategoryAvailableForThisPeriod(outlet, c, start, end)) {
                res.add(c);
            }
        }
        return res;
    }
    
    private Date plusHours(Date date, int hour) {
        LocalDateTime initialLDT = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime afterLDT = initialLDT.plusHours(hour);
        return java.sql.Timestamp.valueOf(afterLDT);
    }

}
