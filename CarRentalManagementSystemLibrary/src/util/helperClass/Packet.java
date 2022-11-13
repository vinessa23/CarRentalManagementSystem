/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helperClass;

import entity.Category;
import entity.RentalRate;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

/**
 *
 * @author vinessa
 */
public class Packet implements Serializable{
    private Category category;
    private List<RentalRate> rentalRates;
    private BigDecimal amount;
    private boolean canReserve;

    public Packet(Category category, List<RentalRate> rentalRates, BigDecimal amount) {
        this.category = category;
        this.rentalRates = rentalRates;
        this.amount = amount;
        if(rentalRates.isEmpty()) {
            canReserve = false;
        } else {
            canReserve = true;
        }
    }
    
    @Override
    public String toString() {
        if(!rentalRates.isEmpty()) {
            return String.format("%20s%40s", getCategory().getCategoryName(), NumberFormat.getCurrencyInstance().format(amount));
        } else {
            return String.format("%20s%40s", getCategory().getCategoryName(), "NOT AVAILABLE");
        }
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<RentalRate> getRentalRates() {
        return rentalRates;
    }

    public void setRentalRates(List<RentalRate> rentalRates) {
        this.rentalRates = rentalRates;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isCanReserve() {
        return canReserve;
    }

    public void setCanReserve(boolean canReserve) {
        this.canReserve = canReserve;
    }
}
