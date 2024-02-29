package com.example.social.controller;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.example.social.dto.response.MessageResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * xử lý khi các request body không pass lỗi , và có ngoại lệ
	 * MethodArgumentNotValidException được ném ra nếu ko throw
	 * MethodArgumentNotValidException thì ngoại lệ ConstraintViolationException sẽ
	 * dc ném ra
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		String errorMessage = "MethodArgumentNotValidException : ";

		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			errorMessage += " " + fieldError.getDefaultMessage() + ";";
		}
		return ResponseEntity.badRequest().body(new MessageResponse(errorMessage));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<?> handleValidationException(BindException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		String errorMessage = "BindException : ";

		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			if (fieldError.getDefaultMessage().contains("job")) {
				errorMessage += "job must be a number";
			} else {
				errorMessage += " " + fieldError.getDefaultMessage() + ";";
			}
		}
		return ResponseEntity.badRequest().body(new MessageResponse(errorMessage));
	}

	/**
	 * xử lý khi có lỗi validate entity trc khi thực hiện save của repository
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleValidationException(ConstraintViolationException ex) {
		return ResponseEntity.badRequest().body("Validation error(s): ConstraintViolationException "
				+ ex.getConstraintViolations().stream().findAny().get().getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
		String mes = ex.getMessage();
		if (mes.startsWith("Error:")) {
			return ResponseEntity.badRequest().body(new MessageResponse(mes));
		}
		return ResponseEntity.internalServerError().body(ex);
	}

	@ExceptionHandler(HttpMessageConversionException.class)
	@ResponseBody
	public ResponseEntity<?> handleHttpMessageConversionException(HttpMessageConversionException ex) {
		return ResponseEntity.badRequest().body(new MessageResponse("can't parse to json type"));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
		return ResponseEntity.badRequest().body(new MessageResponse("Email Or Password not match"));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleNumberFormatException(MethodArgumentTypeMismatchException ex) {
		return ResponseEntity.badRequest().body(new MessageResponse("Id must be number"));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
		return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
	}
	
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new MessageResponse(ex.getMessage()));
	}
}
