/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Model;
import entity.Outlet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.enumeration.CarStatusEnum;
import util.exception.CarLicensePlateExistException;
import util.exception.CarNotFoundException;
import util.exception.ModelIsNotEnabledException;
import util.exception.ModelNotFoundException;
import util.exception.OutletNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author YC
 */
@Stateless
public class CarSessionBean implements CarSessionBeanRemote, CarSessionBeanLocal {

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private ModelSessionBeanLocal modelSessionBeanLocal;
    
    @EJB
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @Override
    public Long createNewCar(Long outletId, Long modelId, Car car) throws OutletNotFoundException, ModelIsNotEnabledException, ModelNotFoundException, CarLicensePlateExistException, UnknownPersistenceException {
        try {
            Model model = modelSessionBeanLocal.retrieveModelById(modelId);
            Outlet outlet = outletSessionBeanLocal.retrieveOutletById(outletId);
            
            if(model.getEnabled()) {
                car.setModel(model);
                model.getCars().add(car);
                outlet.getCars().add(car);
                car.setOutlet(outlet);
                em.persist(car);
                em.flush();
                return car.getCarId();
            } else {
                throw new ModelIsNotEnabledException();
            }
        } catch (PersistenceException ex){
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new CarLicensePlateExistException();
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
        } catch (OutletNotFoundException ex) {
            throw new OutletNotFoundException(ex.getMessage());
        } 
    }
    
    @Override
    public List<Car> retrieveAllCars() {
	Query query = em.createQuery("SELECT c FROM Car c ORDER BY c.licensePlate, c.model.category.categoryName, c.model.modelName ASC"); //need to add car category name, make and model name as well
	List<Car> cars = query.getResultList();
	return cars;
    }
    
    @Override
    public Car retrieveCarById(Long id) throws CarNotFoundException {
        Car car = em.find(Car.class, id);
        if(car != null) {
            return car;
        } else {
            throw new CarNotFoundException("Car ID " + id + " does not exist!");
        }
    }
    
    @Override
    public Car retrieveCarByLicensePlate(String licensePlate) throws CarNotFoundException {
        Query query = em.createQuery("SELECT c FROM Car c WHERE c.licensePlate = :inLicensePlate");
        query.setParameter("inLicensePlate", licensePlate);      
        try {
            return (Car) query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CarNotFoundException("Car license plate " + licensePlate + " does not exist!");
        }
    }

    @Override
    public void updateCar(Car car) throws CarNotFoundException {
        
        if(car != null && car.getCarId() != null)
        {
            Car carToUpdate = retrieveCarById(car.getCarId());

            carToUpdate.setLicensePlate(car.getLicensePlate());
            carToUpdate.setColour(car.getColour());
            carToUpdate.setCarStatus(car.getCarStatus());
            carToUpdate.setEnabled(car.getEnabled());
            //need to add associated entity
            carToUpdate.setModel(car.getModel());
            carToUpdate.setOutlet(car.getOutlet());
            carToUpdate.setCurrentCustomer(car.getCurrentCustomer());
        }
        else
        {
            throw new CarNotFoundException("Car ID not provided for car to be updated");
        }
    }
    
    @Override
    public void deleteCar(Long carId) throws CarNotFoundException {
        
        Car carToRemove = retrieveCarById(carId);
        
        //retrieve the associated entity here 
        //if no reservation with car
        
        if(carToRemove.getCarStatus() == CarStatusEnum.REPAIR ||  carToRemove.getCarStatus() == CarStatusEnum.IN_OUTLET) 
        {
            em.remove(carToRemove);
        }
        else
        {
            carToRemove.setEnabled(false);
        }
    } 
}
