package com.celfocus.hiring.kickstarter.exception;

/**
 * @author amjad.afifi
 */
public class CartNotFoundException extends RuntimeException{

    public CartNotFoundException(String message) {
        super(message);
    }
}
