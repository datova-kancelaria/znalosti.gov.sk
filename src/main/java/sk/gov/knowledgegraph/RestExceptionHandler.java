package sk.gov.knowledgegraph;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import sk.gov.knowledgegraph.model.exception.KnowledgeGraphException;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private Gson gson;

    @ExceptionHandler(KnowledgeGraphException.class)
    public ResponseEntity<Object> handleException(final KnowledgeGraphException e, final WebRequest request) {
        final Error body = new Error().setCode(e.getErrorCode().name()).setMessage(e.getErrorCode().getMessage()).setDetails(e.getDetails());
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.valueOf(e.getErrorCode().getCode()), request);
    }


    @ExceptionHandler({ IllegalStateException.class, IllegalArgumentException.class })
    public ResponseEntity<Object> handleException(final RuntimeException e, final WebRequest request) {
        final Error body = new Error().setCode("TECHNICAL_ERROR").setMessage(e.getMessage());
        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
                    MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final Map<String, String> detail = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                detail.put(((FieldError) error).getField(), error.getDefaultMessage());
                detail.put(((FieldError) error).getField(),
                        ((FieldError) error).getRejectedValue() != null ? ((FieldError) error).getRejectedValue().toString() : null);
            } else {
                detail.put(error.getObjectName(), error.getDefaultMessage());
            }
        });
        final Error body = new Error().setMessage("Validation error occurs!").setDetails(detail);
        return handleExceptionInternal(e, body, headers, HttpStatus.BAD_REQUEST, request);
    }


    @Override
    @Nullable
    protected ResponseEntity<Object> handleExceptionInternal(
                    Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (body != null && body instanceof Error && ((Error) body).getCode() != null) {
            Error err = (Error) body;
            switch (err.getCode()) {
            case "OUTPUT_FORMAT_FORMAT_MISSING":
                log.warn("going to handle exception {} with data {}", ex.getMessage(), err.getDetails(), ex);
                break;
            default:
                log.error("going to handle exception: {} with data {}", ex.getMessage(), err.getDetails(), ex);
                break;
            }
        } else {
            log.error("going to handle exception: {}", ex.getMessage(), ex);
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }


    @PostConstruct
    private void init() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Data
    @Accessors(chain = true)
    private static class Error {

        private String message;
        private String code;
        private Map<String, String> details;
    }
}
