package com.sarveshsawant.webdev.exception.custom;

public class FileSizeExceedException extends RuntimeException {
    public FileSizeExceedException(String message) {
        super(message);
    }
}
