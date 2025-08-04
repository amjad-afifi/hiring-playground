package com.celfocus.hiring.kickstarter.exception;

/**
 * @author amjad.afifi
 */
public class BadRequestException extends RuntimeException{

    public BadRequestException(String message){
        super(message);
    }
}
