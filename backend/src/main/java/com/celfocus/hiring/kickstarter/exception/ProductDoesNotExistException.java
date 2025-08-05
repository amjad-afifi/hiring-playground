package com.celfocus.hiring.kickstarter.exception;

/**
 * @author amjad.afifi
 */
public class ProductDoesNotExistException extends RuntimeException{

    public ProductDoesNotExistException(String message){
        super(message);
    }
}
