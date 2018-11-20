package no.kristiania.pgr301.eksamen.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR = "An internal server error occured";
    private Logger logger = LoggerFactory.getLogger(ServerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleUnexpectedExceptions(Exception ex, WebRequest req) {
        logger.error("Exception " + ex.getClass().getSimpleName() + " thrown!", ex);
        return handleExceptionInternal(
                new RuntimeException(INTERNAL_SERVER_ERROR),
                null,
                new HttpHeaders(),
                HttpStatus.valueOf(500),
                req
        );
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
