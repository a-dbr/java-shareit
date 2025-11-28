package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEmailAlreadyUsedException_returnsConflict() {
        EmailAlreadyUsedException ex = new EmailAlreadyUsedException("a@b.com");

        ResponseEntity<ErrorResponse> resp = handler.handleEmailAlreadyUsedException(ex);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).contains("a@b.com");
        assertThat(resp.getBody().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    void handleUserNotFoundException_returnsNotFound() {
        NotFoundException ex = new NotFoundException("not found");

        ResponseEntity<ErrorResponse> resp = handler.handleUserNotFoundException(ex);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("not found");
        assertThat(resp.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void handleAccessDeniedException_returnsForbidden() {
        AccessDeniedException ex = new AccessDeniedException("no access");

        ResponseEntity<ErrorResponse> resp = handler.handleAccessDeniedException(ex);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("no access");
        assertThat(resp.getBody().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void handleIllegalArgumentException_returnsBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");

        ResponseEntity<ErrorResponse> resp = handler.handleIllegalArgumentException(ex);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("bad arg");
        assertThat(resp.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}