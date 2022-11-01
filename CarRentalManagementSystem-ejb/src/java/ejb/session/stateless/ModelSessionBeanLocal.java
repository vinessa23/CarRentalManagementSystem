/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Model;
import java.util.List;
import javax.ejb.Local;
import util.exception.ModelNameExistException;
import util.exception.ModelNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Local
public interface ModelSessionBeanLocal {

    public Long createNewModel(Model model) throws ModelNameExistException, UnknownPersistenceException;

    public List<Model> retrieveAllModels();

    public Model retrieveModelById(Long id) throws ModelNotFoundException;

    public Model retrieveModelByName(String modelName) throws ModelNotFoundException;

    public void updateModel(Model model) throws ModelNotFoundException;

    public void deleteModel(Long modelId) throws ModelNotFoundException;
    
}
