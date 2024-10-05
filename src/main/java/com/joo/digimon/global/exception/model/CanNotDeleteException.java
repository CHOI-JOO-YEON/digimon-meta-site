package com.joo.digimon.global.exception.model;

public class CanNotDeleteException extends RuntimeException {
    public CanNotDeleteException(String message) {
        super(message);
    }
}
