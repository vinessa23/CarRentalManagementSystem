/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Model;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.ModelNameExistException;
import util.exception.ModelNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Stateless
public class ModelSessionBean implements ModelSessionBeanRemote, ModelSessionBeanLocal {

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createNewModel(Model model) throws ModelNameExistException, UnknownPersistenceException {
        try {
            em.persist(model);
            em.flush();
            return model.getModelId();
        } catch (PersistenceException ex){
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new ModelNameExistException();
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
    public List<Model> retrieveAllModels() {
	Query query = em.createQuery("SELECT m FROM Model m ORDER BY m.makeName, m.modelName ASC"); //need to add car category name as well
	List<Model> models = query.getResultList();
	return models;
    }
    
    @Override
    public Model retrieveModelById(Long id) throws ModelNotFoundException {
        Model model = em.find(Model.class, id);
        if(model != null) {
            return model;
        } else {
            throw new ModelNotFoundException("Model ID " + id + " does not exist!");
        }
    }
    
    @Override
    public Model retrieveModelByName(String modelName) throws ModelNotFoundException {
        Query query = em.createQuery("SELECT m FROM Model m WHERE m.modelName = :inName");
        query.setParameter("inName", modelName);      
        try {
            return (Model) query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex)
        {
            throw new ModelNotFoundException("Model name " + modelName + " does not exist!");
        }
    }

    @Override
    public void updateModel(Model model) throws ModelNotFoundException {
        
        if(model != null && model.getModelId()!= null)
        {
            Model modelToUpdate = retrieveModelById(model.getModelId());

            modelToUpdate.setMakeName(model.getMakeName());
            modelToUpdate.setModelName(model.getModelName());
            modelToUpdate.setEnabled(model.getEnabled());
            //need to add associated entity
        }
        else
        {
            throw new ModelNotFoundException("Model ID not provided for model to be updated");
        }
    }
    
    @Override
    public void deleteModel(Long modelId) throws ModelNotFoundException {
        
        Model modelToRemove = retrieveModelById(modelId);
        
        //retrieve the associated entity here 
        //List<Car> cars = modelToRemove.getCars();
        
        //if(cars.isEmpty())
        //{
            em.remove(modelToRemove);
        //}
        //else
        //{
        //    modelToRemove.setEnabled(false);
        //}
    }
}
