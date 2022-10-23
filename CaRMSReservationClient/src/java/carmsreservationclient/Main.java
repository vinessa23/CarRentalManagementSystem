/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import javax.ejb.EJB;


public class Main {
    //MUST INJECT THE SB TO Main.java AND NOT TO MainApp.java
    @EJB
    private static CustomerSessionBeanRemote customerSessionBeanRemote;


    public static void main(String[] args) {
        //pass in the injected SB here to the main app
        MainApp mainApp = new MainApp(customerSessionBeanRemote);
        mainApp.runApp();
    }
    
}
