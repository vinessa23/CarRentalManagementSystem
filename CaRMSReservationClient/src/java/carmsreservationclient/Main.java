/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsreservationclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CategorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ModelSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import javax.ejb.EJB;


public class Main {

    @EJB
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;

    @EJB
    private static ModelSessionBeanRemote modelSessionBean;

    @EJB
    private static CategorySessionBeanRemote categorySessionBeanRemote;

    @EJB
    private static CarSessionBeanRemote carSessionBeanRemote;

    @EJB
    private static RentalRateSessionBeanRemote rentalRateSessionBeanRemote;

    @EJB
    private static OutletSessionBeanRemote outletSessionBeanRemote;

    @EJB
    private static CustomerSessionBeanRemote customerSessionBeanRemote;

    
    

    public static void main(String[] args) {
        //pass in the injected SB here to the main app
        MainApp mainApp = new MainApp(reservationSessionBeanRemote, modelSessionBean, categorySessionBeanRemote, carSessionBeanRemote, rentalRateSessionBeanRemote, outletSessionBeanRemote, customerSessionBeanRemote);
        mainApp.runApp();
    }
    
}
