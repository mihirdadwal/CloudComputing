package com.sarveshsawant.webdev.exception.custom;

public class PasswordIncorrectException extends RuntimeException{
    PasswordIncorrectException(String message){
        super(message);
    }
}
