package com.techchallenge.scheduling_service.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Controlador de conselho (Advice) global para tratamento de exceções nos recursos da API.
 * Esta classe intercepta exceções lançadas por qualquer Controller e padroniza a resposta
 * de erro utilizando o objeto {@link ErrorMessage}.
 * * @author Erick Calazães
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manipula exceções de recursos não encontrados no banco de dados.
     * * @param e       A exceção {@link EntityNotFoundException} lançada.
     * @param request O objeto da requisição para extrair a URI acessada.
     * @return Uma resposta com status 404 (Not Found) e os detalhes do erro.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> entityNotFound(EntityNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorMessage err = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                "Recurso não encontrado",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Manipula falhas de validação de argumentos (@Valid) nos DTOs de entrada.
     * Extrai a primeira mensagem de erro definida nas anotações do Bean Validation.
     * * @param e       A exceção {@link MethodArgumentNotValidException} lançada.
     * @param request O objeto da requisição para extrair a URI acessada.
     * @return Uma resposta com status 400 (Bad Request) e o detalhe do campo inválido.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        ErrorMessage err = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                "Erro de validação",
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Captura qualquer exceção não tratada especificamente pelos métodos anteriores.
     * Funciona como uma rede de segurança para evitar que stacktraces internos
     * sejam expostos ao cliente da API, retornando um erro 500 genérico.
     * * @param e       A exceção genérica lançada.
     * @param request O objeto da requisição para extrair a URI acessada.
     * @return Uma resposta com status 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> standardError(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorMessage err = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                "Erro interno no servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        e.printStackTrace();
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Manipula erros de violação de integridade de dados (ex: tentativa de inserir um
     * registro duplicado em campos com restrição UNIQUE no banco).
     * * @param e       A exceção {@link DataIntegrityViolationException} lançada.
     * @param request O objeto da requisição para extrair a URI acessada.
     * @return Uma resposta com status 409 (Conflict).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> dataIntegrity(DataIntegrityViolationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT; // 409 é o código correto para conflito de dados

        // Tentamos extrair uma mensagem mais limpa, caso contrário usamos uma padrão
        String message = "Recurso já cadastrado ou violação de integridade.";
        if (e.getMostSpecificCause().getMessage().contains("duplicate key")) {
            message = "Os dados informados já existem no sistema (Violação de Duplicidade).";
        }

        ErrorMessage err = new ErrorMessage(
                LocalDateTime.now(),
                status.value(),
                "Conflito de dados",
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}
