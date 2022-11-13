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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.BookingStatus;
import util.enumeration.PaymentStatus;

/**
 *
 * @author YC
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PaymentStatus paymentStatus;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date endDate;
    @Column(precision = 19, scale = 2)
    @DecimalMin("0.00")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private BookingStatus bookingStatus = BookingStatus.ACTIVE;
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancellationTime;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String ccNum;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String nameOnCard;
    @Column(nullable = false, length = 3)
    @NotNull
    @Size(min = 1, max = 3)
    private String cvv;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @Future
    @NotNull
    private Date expiryDate;
    @Column(nullable = false)
    @NotNull
    private boolean needTransit = false;
    @Column(nullable = false)
    @NotNull
    private boolean isTransitCompleted = false;
    @Column(length = 32)
    @Size(min = 1, max = 32)
    private String pickUpCustomerName;
    @Column(length = 32)
    @Size(min = 1, max = 32)
    private String pickUpCustomerEmail;
    @Column(length = 32)
    @Size(min = 1, max = 32)
    private String returnCustomerName;
    @Column(length = 32)
    @Size(min = 1, max = 32)
    private String returnCustomerEmail;
    @Column(length = 32)
    @Size(min = 1, max = 32)
    private String partnerCustomerName;
    @Column(length = 32)
    @Size(min = 1, max = 32)
    private String partnerCustomerEmail;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer bookingCustomer;

    @OneToOne
    private Car car;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Employee driver;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Outlet pickupOutlet;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Outlet returnOutlet;
    
    @ManyToMany
    private List<RentalRate> rentalRates;

    public Reservation() {
        rentalRates = new ArrayList<>();
    }

    public Reservation(PaymentStatus paymentStatus, Date startDate, Date endDate, String ccNum, String nameOnCard, String cvv, Date expiryDate) {
        this();
        this.paymentStatus = paymentStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ccNum = ccNum;
        this.nameOnCard = nameOnCard;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    public Reservation(PaymentStatus paymentStatus, Date startDate, Date endDate, BigDecimal totalAmount, String ccNum, String nameOnCard, String cvv, Date expiryDate) {
        this.paymentStatus = paymentStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.ccNum = ccNum;
        this.nameOnCard = nameOnCard;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }
    
    

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Date getCancellationTime() {
        return cancellationTime;
    }

    public void setCancellationTime(Date cancellationTime) {
        this.cancellationTime = cancellationTime;
    }

    public String getCcNum() {
        return ccNum;
    }

    public void setCcNum(String ccNum) {
        this.ccNum = ccNum;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isIsTransitCompleted() {
        return isTransitCompleted;
    }

    public void setIsTransitCompleted(boolean isTransitCompleted) {
        this.isTransitCompleted = isTransitCompleted;
    }

    @XmlTransient
    public Customer getBookingCustomer() {
        return bookingCustomer;
    }

    public void setBookingCustomer(Customer bookingCustomer) {
        this.bookingCustomer = bookingCustomer;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Employee getDriver() {
        return driver;
    }

    public void setDriver(Employee driver) {
        this.driver = driver;
    }

    public Outlet getPickupOutlet() {
        return pickupOutlet;
    }

    public void setPickupOutlet(Outlet pickupOutlet) {
        this.pickupOutlet = pickupOutlet;
    }

    public Outlet getReturnOutlet() {
        return returnOutlet;
    }

    public void setReturnOutlet(Outlet returnOutlet) {
        this.returnOutlet = returnOutlet;
    }

    public String getPickUpCustomerName() {
        return pickUpCustomerName;
    }

    public void setPickUpCustomerName(String pickUpCustomerName) {
        this.pickUpCustomerName = pickUpCustomerName;
    }

    public String getPickUpCustomerEmail() {
        return pickUpCustomerEmail;
    }

    public void setPickUpCustomerEmail(String pickUpCustomerEmail) {
        this.pickUpCustomerEmail = pickUpCustomerEmail;
    }

    public String getReturnCustomerName() {
        return returnCustomerName;
    }

    public void setReturnCustomerName(String returnCustomerName) {
        this.returnCustomerName = returnCustomerName;
    }

    public String getReturnCustomerEmail() {
        return returnCustomerEmail;
    }

    public void setReturnCustomerEmail(String returnCustomerEmail) {
        this.returnCustomerEmail = returnCustomerEmail;
    }

    public String getPartnerCustomerName() {
        return partnerCustomerName;
    }

    public void setPartnerCustomerName(String partnerCustomerName) {
        this.partnerCustomerName = partnerCustomerName;
    }

    public String getPartnerCustomerEmail() {
        return partnerCustomerEmail;
    }

    public void setPartnerCustomerEmail(String partnerCustomerEmail) {
        this.partnerCustomerEmail = partnerCustomerEmail;
    }

    public List<RentalRate> getRentalRates() {
        return rentalRates;
    }

    public void setRentalRates(List<RentalRate> rentalRates) {
        this.rentalRates = rentalRates;
    }

    public boolean isNeedTransit() {
        return needTransit;
    }

    public void setNeedTransit(boolean needTransit) {
        this.needTransit = needTransit;
    }

}
