package gyurix.countryroute.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@RestController
public class ErrorHandler extends ResponseEntityExceptionHandler implements ErrorController {
  private final ObjectMapper mapper = new ObjectMapper();

  @GetMapping(value = "/error", produces = "application/json")
  public ResponseEntity<ObjectNode> getMissingEndpoint() {
    return ResponseEntity.status(NOT_FOUND)
      .body(mapper.createObjectNode()
        .put("error", NOT_FOUND.toString())
        .put("reason", "The provided endpoint was not found")
      );
  }

  @ExceptionHandler(Throwable.class)
  public final ResponseEntity<ObjectNode> handleAllExceptions(Throwable ex) {
    logger.info("Got exception", ex);
    if (ex instanceof ResponseStatusException) {
      ResponseStatusException rse = (ResponseStatusException) ex;
      return ResponseEntity.status(rse.getStatus())
        .header("Content-Type", "application/json")
        .body(mapper.createObjectNode()
          .put("error", rse.getStatus().toString())
          .put("reason", rse.getReason())
        );
    }
    logger.error("Detected Unexpected Error", ex);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
      .header("Content-Type", "application/json")
      .body(mapper.createObjectNode()
        .put("error", INTERNAL_SERVER_ERROR.toString())
        .put("reason", "Unexpected Error")
      );
  }
}
