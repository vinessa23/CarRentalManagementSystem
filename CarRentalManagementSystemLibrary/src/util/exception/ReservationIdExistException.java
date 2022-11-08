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
public class ReservationIdExistException extends Exception {

    /**
     * Creates a new instance of <code>ReservationIdExistsException</code>
     * without detail message.
     */
    public ReservationIdExistException() {
    }

    /**
     * Constructs an instance of <code>ReservationIdExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ReservationIdExistException(String msg) {
        super(msg);
    }
}
