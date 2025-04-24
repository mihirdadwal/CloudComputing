package com.sarveshsawant.webdev.exception.custom;

public class FileEmptyException extends RuntimeException {
    public FileEmptyException(String message) {
        super(message);
    }
}
