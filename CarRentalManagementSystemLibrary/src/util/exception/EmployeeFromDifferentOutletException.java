/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author vinessa
 */
public class EmployeeFromDifferentOutletException extends Exception {

    /**
     * Creates a new instance of
     * <code>EmployeeFromDifferentOutletException</code> without detail message.
     */
    public EmployeeFromDifferentOutletException() {
    }

    /**
     * Constructs an instance of
     * <code>EmployeeFromDifferentOutletException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public EmployeeFromDifferentOutletException(String msg) {
        super(msg);
    }
}
