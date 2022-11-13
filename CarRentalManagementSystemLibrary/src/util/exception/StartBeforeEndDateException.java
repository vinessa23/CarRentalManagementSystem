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
public class StartBeforeEndDateException extends Exception {

    /**
     * Creates a new instance of <code>StartBeforeEndDateException</code>
     * without detail message.
     */
    public StartBeforeEndDateException() {
    }

    /**
     * Constructs an instance of <code>StartBeforeEndDateException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public StartBeforeEndDateException(String msg) {
        super(msg);
    }
}
