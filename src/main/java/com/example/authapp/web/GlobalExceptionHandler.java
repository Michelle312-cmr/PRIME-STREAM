package com.example.authapp.web;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", "Page non trouvée");
        model.addAttribute("path", ex.getRequestURL());
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        return "errors/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("message", "Une erreur interne s'est produite");
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        return "errors/500";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("message", "Requête invalide");
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        return "errors/400";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("message", "Accès refusé");
        model.addAttribute("error", "Vous n'avez pas les permissions nécessaires");
        model.addAttribute("timestamp", LocalDateTime.now().format(formatter));
        return "errors/403";
    }
}

class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
