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
public class ReturnBeforePickupDateException extends Exception {

    /**
     * Creates a new instance of <code>ReturnBeforePickupDateException</code>
     * without detail message.
     */
    public ReturnBeforePickupDateException() {
    }

    /**
     * Constructs an instance of <code>ReturnBeforePickupDateException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ReturnBeforePickupDateException(String msg) {
        super(msg);
    }
}
