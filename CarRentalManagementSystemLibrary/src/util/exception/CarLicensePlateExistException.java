/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author YC
 */
public class CarLicensePlateExistException extends Exception {

    /**
     * Creates a new instance of <code>CarLicensePlateExistException</code>
     * without detail message.
     */
    public CarLicensePlateExistException() {
    }

    /**
     * Constructs an instance of <code>CarLicensePlateExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CarLicensePlateExistException(String msg) {
        super(msg);
    }
}
