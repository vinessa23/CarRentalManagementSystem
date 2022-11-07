/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.comparator;

import entity.Category;
import entity.RentalRate;
import java.util.Comparator;
import util.enumeration.RentalRateType;

/**
 *
 * @author vinessa
 */
public class RentalRateComparator implements Comparator<RentalRate>{
    
    //default shld be at the very bottom
    @Override
    public int compare(RentalRate r1, RentalRate r2) {
        Category cat1 = r1.getCategory();
        Category cat2 = r2.getCategory();
        
        if(!cat1.equals(cat2)) {
            return cat1.getCategoryName().compareTo(cat2.getCategoryName());
        } else {
        // DEFAULT is at the bottom since no starting date!
            if(r1.getType() != RentalRateType.DEFAULT && r2.getType() == RentalRateType.DEFAULT) {
                return 1;
            } else if (r1.getType() == RentalRateType.DEFAULT && r2.getType() != RentalRateType.DEFAULT) {
                return -1;
            } else if (r1.getType() == RentalRateType.DEFAULT && r2.getType() == RentalRateType.DEFAULT) {
                return 0;
            } else {
                return r1.getStartDate().compareTo(r2.getStartDate());
            }
        }
    }
    
}
