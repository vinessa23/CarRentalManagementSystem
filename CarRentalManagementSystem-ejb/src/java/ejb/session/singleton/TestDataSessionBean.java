/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CarSessionBeanLocal;
import entity.Car;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author YC
 */
@Singleton
@LocalBean
@Startup
public class TestDataSessionBean {

    @EJB
    private CarSessionBeanLocal carSessionBeanLocal;

    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    public void viewAllCars() {
        List<Car> cars = carSessionBeanLocal.retrieveAllCars();
        for (Car car : cars) {
            System.out.println(car.getModel().getCategory().getCategoryName() + " " + car.getModel().getMakeName()
             + " " + car.getModel().getModelName() + " " + car.getLicensePlate());
        }
    }
}
