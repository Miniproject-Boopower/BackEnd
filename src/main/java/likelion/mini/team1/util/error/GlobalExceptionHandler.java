package likelion.mini.team1.util.error;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	public static final String TRACE = "trace";

	@Value("${error.print-stack-trace}")
	private boolean printStackTrace;

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		return buildErrorResponse(ex, ex.getMessage(), HttpStatus.valueOf(statusCode.value()), request);
	}

	private ResponseEntity<Object> buildErrorResponse(Exception exception,
		String message,
		HttpStatus httpStatus,
		WebRequest request) {
		ErrorResponseDto errorResponseDto = new ErrorResponseDto(httpStatus.value(), message, LocalDateTime.now());
		if (printStackTrace && isTraceOn(request)) {
			errorResponseDto.setStackTrace(ExceptionUtils.getStackTrace(exception));
		}
		return ResponseEntity.status(httpStatus).body(errorResponseDto);
	}

	private boolean isTraceOn(WebRequest request) {
		String[] value = request.getParameterValues(TRACE);
		return Objects.nonNull(value)
			&& value.length > 0
			&& value[0].contentEquals("true");
	}

	// 500 Uncaught Exception
	@ExceptionHandler(Exception.class)
	@Hidden
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
		log.error("Internal error occurred", exception);
		return buildErrorResponse(exception, exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	// 403 Access Denied Exception
	@ExceptionHandler(AccessDeniedException.class)
	@Hidden
	@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden
	public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {
		log.error("Access denied", exception);
		return buildErrorResponse(exception, "Access denied", HttpStatus.FORBIDDEN, request);
	}

	// 400 Runtime Exception
	@ExceptionHandler(RuntimeException.class)
	@Hidden
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException exception, WebRequest request) {
		log.error("Runtime Exception", exception);
		return buildErrorResponse(exception, exception.getMessage(), HttpStatus.BAD_REQUEST, request);
	}
}