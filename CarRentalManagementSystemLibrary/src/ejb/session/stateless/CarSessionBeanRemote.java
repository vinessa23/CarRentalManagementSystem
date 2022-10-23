/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CarLicensePlateExistException;
import util.exception.CarNotFoundException;
import util.exception.ModelNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Remote
public interface CarSessionBeanRemote {
    
    public Long createNewCar(Long modelId, Car car) throws ModelNotFoundException, CarLicensePlateExistException, UnknownPersistenceException;
    
    public List<Car> retrieveAllCars();
    
    public Car retrieveCarById(Long id) throws CarNotFoundException;
    
    public Car retrieveCarByLicensePlate(String licensePlate) throws CarNotFoundException;
    
    public void updateCar(Car car) throws CarNotFoundException;
    
    public void deleteCar(Long carId) throws CarNotFoundException;
    
}
