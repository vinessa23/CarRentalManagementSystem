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
public class InvalidEmployeeRoleException extends Exception {

    /**
     * Creates a new instance of <code>InvalidEmployeeRoleException</code>
     * without detail message.
     */
    public InvalidEmployeeRoleException() {
    }

    /**
     * Constructs an instance of <code>InvalidEmployeeRoleException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidEmployeeRoleException(String msg) {
        super(msg);
    }
}
