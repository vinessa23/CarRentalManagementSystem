/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.Outlet;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CategoryNameExistException;
import util.exception.CategoryNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Remote
public interface CategorySessionBeanRemote {
    
    public Long createNewCategory(Category category) throws CategoryNameExistException, UnknownPersistenceException;
    
    public List<Category> retrieveAllCategories();
        
    public Category retrieveCategoryById(Long id) throws CategoryNotFoundException;

    public Category retrieveCategoryByName(String name) throws CategoryNotFoundException;
    
    public List<Category> categoriesAvailableForThisPeriod(Outlet outlet, Date start, Date end);
}
