package com.celfocus.hiring.kickstarter.exception;

/**
 * @author amjad.afifi
 */
public class InsufficientStockException extends RuntimeException{

    public InsufficientStockException(String message){
        super(message);
    }
}
