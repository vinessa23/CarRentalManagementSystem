/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import entity.Category;
import entity.Outlet;
import entity.RentalRate;
import java.util.Date;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.Remote;

/**
 *
 * @author vinessa
 */
@Remote
public interface ReservationSessionBeanRemote {
    public List<Pair<Category, List<RentalRate>>> searchCar(Category category, Date start, Date end, Outlet pickupOutlet, Outlet returnOutlet);
}
