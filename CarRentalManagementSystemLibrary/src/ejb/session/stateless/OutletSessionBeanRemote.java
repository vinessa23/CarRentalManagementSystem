/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Outlet;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.OutletNameExistException;
import util.exception.OutletNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Remote
public interface OutletSessionBeanRemote {
    
    public Long createNewOutlet(Outlet outlet) throws OutletNameExistException, UnknownPersistenceException, InputDataValidationException;

    public List<Outlet> retrieveAllOutlets();
    
    public Outlet retrieveOutletById(Long id) throws OutletNotFoundException;
    
    public Outlet retrieveOutletByName(String name) throws OutletNotFoundException;
    
}
