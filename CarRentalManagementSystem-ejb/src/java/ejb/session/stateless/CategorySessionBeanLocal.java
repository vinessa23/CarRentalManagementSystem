/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import java.util.List;
import javax.ejb.Local;
import util.exception.CategoryNameExistException;
import util.exception.CategoryNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Local
public interface CategorySessionBeanLocal {

    public Long createNewCategory(Category category) throws CategoryNameExistException, UnknownPersistenceException;

    public List<Category> retrieveAllCategories();
    
    public Category retrieveCategoryById(Long id) throws CategoryNotFoundException;

    public Category retrieveCategoryByName(String name) throws CategoryNotFoundException;
    
}