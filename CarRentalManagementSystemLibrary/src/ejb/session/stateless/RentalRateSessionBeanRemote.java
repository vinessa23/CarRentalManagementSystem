/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalRate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RentalRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author vinessa
 */
@Remote
public interface RentalRateSessionBeanRemote {
    public Long createNewRentalRate(RentalRate rentalRate, Long categoryId) throws UnknownPersistenceException;

    public List<RentalRate> retrieveAllRentalRates() throws RentalRateNotFoundException;

    public RentalRate retrieveRentalRateById(Long id) throws RentalRateNotFoundException;

    public void updateRentalRate(RentalRate rentalRate) throws RentalRateNotFoundException;

    public void deleteRentalRate(Long rentalRateId) throws RentalRateNotFoundException;
    
}
