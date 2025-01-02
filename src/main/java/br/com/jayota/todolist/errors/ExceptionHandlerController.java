package br.com.jayota.todolist.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // Anotação usada para definir classes globais no momento de tratamento de exceções   
public class ExceptionHandlerController { // classe utilizada para tratar exceções/erros
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpNotReadableException(HttpMessageNotReadableException e) { // caso o erro seja este ->
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMostSpecificCause().getMessage()); // retornamos a mensagem que criamos para tratar essa situação
    }
}
