/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carmsreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import entity.Customer;
import java.util.Scanner;

/**
 *
 * @author vinessa
 */
public class MainApp {
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    
    private Customer currentCustomer;
    
    public MainApp() 
    {
        currentCustomer = null;
    }
    
    //initialise the remote SB attributes
    public MainApp(CustomerSessionBeanRemote customerSessionBeanRemote) 
    {
        this();
        this.customerSessionBeanRemote = customerSessionBeanRemote;
    }
    
    public void runApp()
    {
//        Scanner scanner = new Scanner(System.in);
//        Integer response = 0;
//        
//        while(true)
//        {
//            System.out.println("*** Welcome to Merlion Car Rental ***\n");
//            
//            if(currentCustomer != null)
//            {
//                System.out.println("You are login as " + currentCustomer.getName() + "\n");
//            }
//            //can only login/sign up if the customer is currently NOT logged in
//            else
//            {            
//                System.out.println("1: Sign Up");
//                System.out.println("2: Login");
//            }
//            
//            System.out.println("3: Search Car");
//            System.out.println("4: Exit\n");
//            response = 0;
//            
//            //if answer not valid, then prompt user again
//            while(response < 1 || response > 4)
//            {
//                System.out.print("> ");
//
//                response = scanner.nextInt();
//    
    }

}
