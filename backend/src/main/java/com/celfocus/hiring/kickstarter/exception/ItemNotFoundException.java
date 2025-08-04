package com.celfocus.hiring.kickstarter.exception;

/**
 * @author amjad.afifi
 */
public class ItemNotFoundException extends RuntimeException{

    public ItemNotFoundException(String message){
        super(message);
    }
}
