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
public class TransitRecordNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>TransitRecordNotFoundException</code>
     * without detail message.
     */
    public TransitRecordNotFoundException() {
    }

    /**
     * Constructs an instance of <code>TransitRecordNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public TransitRecordNotFoundException(String msg) {
        super(msg);
    }
}
