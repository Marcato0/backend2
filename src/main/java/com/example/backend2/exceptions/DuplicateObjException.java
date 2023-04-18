    package com.example.backend2.exceptions;

    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.ResponseStatus;

    @ResponseStatus(HttpStatus.CONFLICT)
    public class DuplicateObjException extends RuntimeException {
        public DuplicateObjException(String message) {
            super(message);
        }
    }
