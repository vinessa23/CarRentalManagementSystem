/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import util.enumeration.RentalRateType;

/**
 *
 * @author vinessa
 */
@Entity
public class RentalRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalRateId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal ratePerDay;
    @Column(nullable = false)
    private Boolean enabled;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Enumerated(EnumType.STRING)
    private RentalRateType type;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Category category;
    
    @ManyToMany(mappedBy = "rentalRates")
    private List<Reservation> reservations;    

    public RentalRate() {
        enabled = true;
        reservations = new ArrayList<>();
    }

    public RentalRate(String name, BigDecimal ratePerDay, Boolean enabled, RentalRateType type) {
        this.name = name;
        this.ratePerDay = ratePerDay;
        this.enabled = enabled;
        this.type = type;
    }

    public RentalRate(String name, BigDecimal ratePerDay, Boolean enabled, Date startDate, Date endDate, RentalRateType type) {
        this.name = name;
        this.ratePerDay = ratePerDay;
        this.enabled = enabled;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public Long getRentalRateId() {
        return rentalRateId;
    }

    public void setRentalRateId(Long rentalRateId) {
        this.rentalRateId = rentalRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getRentalRateId() != null ? getRentalRateId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the rentalRateId fields are not set
        if (!(object instanceof RentalRate)) {
            return false;
        }
        RentalRate other = (RentalRate) object;
        if ((this.getRentalRateId() == null && other.getRentalRateId() != null) || (this.getRentalRateId() != null && !this.rentalRateId.equals(other.rentalRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RentalRate[ id=" + getRentalRateId() + " ]";
    }

    public BigDecimal getRatePerDay() {
        return ratePerDay;
    }

    public void setRatePerDay(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public RentalRateType getType() {
        return type;
    }

    public void setType(RentalRateType type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

  
    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
  public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
