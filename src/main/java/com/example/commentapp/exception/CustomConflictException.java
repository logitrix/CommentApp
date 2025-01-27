package com.example.commentapp.exception;


public class CustomConflictException extends Exception {

    private String conflict;

    public CustomConflictException(String error) {
        super(error);
        this.conflict = error;
    }

    public String getErrorMessage() {
        return conflict;
    }
}
