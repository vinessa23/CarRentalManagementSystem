/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Category;
import entity.Outlet;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.CategoryNameExistException;
import util.exception.CategoryNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Stateless
public class CategorySessionBean implements CategorySessionBeanRemote, CategorySessionBeanLocal {

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
    
   /* private boolean isCategoryAvailableForThisPeriod(Outlet outlet, Category category, Date start, Date end) {
        int carAvailable = numCarsForCategoryAndOutlet(category, outlet);
        
    }
    
    private int numOverlappingReservations(Outlet outlet, Category category, Date start, Date end) {
        
    }
    
    private List<Reservation> retrieveAllReservations() throw {
        Query query = em.createQuery("SELECT r FROM Reservation r");
        try {
            List<Reservation> reservations = query.getResultList();
            return reservations;
        } catch (NoResultException ex) {
            return new 
        }
    }

    public List<Category> categoriesAvailableForThisPeriod(Outlet outlet, Date start, Date end) {
        
    }
*/
}
