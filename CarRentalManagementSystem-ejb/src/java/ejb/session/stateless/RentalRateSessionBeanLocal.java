/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.RentalRate;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.RentalRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Local
public interface RentalRateSessionBeanLocal {

    public Long createNewRentalRate(RentalRate rentalRate, Long categoryId) throws UnknownPersistenceException;

    public List<RentalRate> retrieveAllRentalRates() throws RentalRateNotFoundException;

    public RentalRate retrieveRentalRateById(Long id) throws RentalRateNotFoundException;
    
    public RentalRate retrieveRentalRateByName(String name) throws RentalRateNotFoundException;

    public void updateRentalRate(RentalRate rentalRate) throws RentalRateNotFoundException;

    public void deleteRentalRate(Long rentalRateId) throws RentalRateNotFoundException;

    public List<RentalRate> calculateRentalRate(Category category, Date startingDate, Date endDate) throws RentalRateNotFoundException;
    
}
