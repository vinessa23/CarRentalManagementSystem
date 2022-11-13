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
public class ModelIsNotEnabledException extends Exception {

    /**
     * Creates a new instance of <code>ModelIsNotEnabledException</code> without
     * detail message.
     */
    public ModelIsNotEnabledException() {
    }

    /**
     * Constructs an instance of <code>ModelIsNotEnabledException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ModelIsNotEnabledException(String msg) {
        super(msg);
    }
}
