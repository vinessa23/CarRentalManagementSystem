/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helperClass;

import entity.Category;
import entity.RentalRate;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author vinessa
 */
public class Packet {
    Category category;
    List<RentalRate> rentalRates;
    BigDecimal amount;

    public Packet(Category category, List<RentalRate> rentalRates, BigDecimal amount) {
        this.category = category;
        this.rentalRates = rentalRates;
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        if(!rentalRates.isEmpty()) {
            return String.format("%20s%40s", category.getCategoryName(), amount.toString());
        } else {
            return String.format("%20s%40s", category.getCategoryName(), "NOT AVAILABLE");
        }
    }
}
