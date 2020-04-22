package com.example.cookbook.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "User is not authorized")
public class NotAuthorizedException extends Exception {
}
