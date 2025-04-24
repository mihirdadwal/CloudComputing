package com.sarveshsawant.webdev.exception.custom;

public class ImageAlreadyExists extends RuntimeException{
    public ImageAlreadyExists(String message){
        super(message);
    }
}
