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
public class CategoryNameExistException extends Exception{

    /**
     * Creates a new instance of <code>CategoryNameExistException</code> without
     * detail message.
     */
    public CategoryNameExistException() {
    }

    /**
     * Constructs an instance of <code>CategoryNameExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CategoryNameExistException(String msg) {
        super(msg);
    }
}
