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
public class ReservationAlreadyCancelledException extends Exception {

    /**
     * Creates a new instance of
     * <code>ReservationAlreadyCancelledException</code> without detail message.
     */
    public ReservationAlreadyCancelledException() {
    }

    /**
     * Constructs an instance of
     * <code>ReservationAlreadyCancelledException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public ReservationAlreadyCancelledException(String msg) {
        super(msg);
    }
}
