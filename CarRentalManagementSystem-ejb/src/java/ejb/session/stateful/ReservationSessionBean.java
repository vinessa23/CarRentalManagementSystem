/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.RentalRateSessionBeanLocal;
import entity.Category;
import entity.Outlet;
import entity.RentalRate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.RentalRateNotFoundException;

/**
 *
 * @author vinessa
 */
@Stateful
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "RentalRateSessionBeanLocal")
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

    @EJB(name = "CategorySessionBeanLocal")
    private CategorySessionBeanLocal categorySessionBeanLocal;
    
    @PersistenceContext(unitName = "CarRentalManagementSystem-ejbPU")
    private EntityManager em;

    
    public List<Pair<Category, List<RentalRate>>> searchCar(Category category, Date start, Date end, Outlet pickupOutlet, Outlet returnOutlet) {
        List<Category> categories = categorySessionBeanLocal.categoriesAvailableForThisPeriod(pickupOutlet, start, end);
        List<Pair<Category, List<RentalRate>>> res = new ArrayList<>();
        for(Category c : categories) {
            try {
                List<RentalRate> r = rentalRateSessionBeanLocal.calculateRentalRate(c, start, end);
                Pair<Category, List<RentalRate>> pair = new Pair<Category, List<RentalRate>>(c, r);
                res.add(pair);
            } catch (RentalRateNotFoundException ex) {
                List<RentalRate> r = new ArrayList<>();
                Pair<Category, List<RentalRate>> pair = new Pair<Category, List<RentalRate>>(c, r);
                res.add(pair);
            }
        }
        return res;
    }
    
    
}
