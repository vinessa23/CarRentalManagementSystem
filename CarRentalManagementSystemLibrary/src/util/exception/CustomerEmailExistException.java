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
public class CustomerEmailExistException extends Exception {

    /**
     * Creates a new instance of <code>EmailAlreadyExistException</code> without
     * detail message.
     */
    public CustomerEmailExistException() {
    }

    /**
     * Constructs an instance of <code>EmailAlreadyExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CustomerEmailExistException(String msg) {
        super(msg);
    }
}
