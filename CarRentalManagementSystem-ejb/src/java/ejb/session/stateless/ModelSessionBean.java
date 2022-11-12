/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Category;
import entity.Model;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.CategoryNotFoundException;
import util.exception.ModelNameExistException;
import util.exception.ModelNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Stateless
public class ModelSessionBean implements ModelSessionBeanRemote, ModelSessionBeanLocal {

    @EJB
    private CategorySessionBeanLocal categorySessionBeanLocal;

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewModel(Long categoryId, Model model) throws CategoryNotFoundException, ModelNameExistException, UnknownPersistenceException {
        try {
            Category category = categorySessionBeanLocal.retrieveCategoryById(categoryId);
            category.getModels().add(model);
            model.setCategory(category);
            em.persist(model);
            em.flush();
            return model.getModelId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new ModelNameExistException("Model name already exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        } catch (CategoryNotFoundException ex) {
            throw new CategoryNotFoundException();
        }
    }

    @Override
    public List<Model> retrieveAllModels() {
        Query query = em.createQuery("SELECT m FROM Model m ORDER BY m.category.categoryName, m.makeName, m.modelName ASC"); //need to add car category name as well
        List<Model> models = query.getResultList();
        List<Model> res = new ArrayList<>();
        for (Model m : models) {
            if (m.getEnabled()) {
                m.getCars().size();
                res.add(m);
            }
        }
        return res;
    }

    @Override
    public Model retrieveModelById(Long id) throws ModelNotFoundException {
        Model model = em.find(Model.class, id);
        if (model != null) {
            model.getCars().size();
            return model;
        } else {
            throw new ModelNotFoundException("Model ID " + id + " does not exist!");
        }
    }

    @Override
    public Model retrieveModelByMakeModelName(String makeName, String modelName) throws ModelNotFoundException {
        Query query = em.createQuery("SELECT m FROM Model m WHERE m.makeName = :inMakeName AND m.modelName = :inModelName");
        query.setParameter("inMakeName", makeName);
        query.setParameter("inModelName", modelName);
        try {
            Model model = (Model) query.getSingleResult();
            model.getCars().size();
            return model;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new ModelNotFoundException("Make name " + makeName + " and Model name " + modelName + " does not exist!");
        }
    }

    @Override
    public void updateModel(Model model) throws ModelNotFoundException {

        if (model != null && model.getModelId() != null) {
            Model modelToUpdate = retrieveModelById(model.getModelId());

            modelToUpdate.setMakeName(model.getMakeName());
            modelToUpdate.setModelName(model.getModelName());
            modelToUpdate.setEnabled(model.getEnabled());
            modelToUpdate.setCars(model.getCars());
            modelToUpdate.setCategory(model.getCategory());
        } else {
            throw new ModelNotFoundException("Model ID not provided for model to be updated");
        }
    }

    @Override
    public void deleteModel(Long modelId) throws ModelNotFoundException {
        try {
            Model modelToRemove = retrieveModelById(modelId);

            //retrieve the associated entity here 
            List<Car> cars = modelToRemove.getCars();

            if (cars.isEmpty()) {
                em.remove(modelToRemove);
            } else {
                modelToRemove.setEnabled(false);
            }
        } catch (ModelNotFoundException ex) {
            throw new ModelNotFoundException("Model of ID: " + modelId + " not found!");
        }
    }
}
