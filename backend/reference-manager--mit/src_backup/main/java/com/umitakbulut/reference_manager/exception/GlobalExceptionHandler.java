package com.umitakbulut.reference_manager.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("❌ Geçersiz argüman hatası: ", e);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Geçersiz veri");
        errorResponse.put("message", e.getMessage());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        log.error("❌ Kayıt bulunamadı hatası: ", e);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Kayıt bulunamadı");
        errorResponse.put("message", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("❌ Genel hata: ", e);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Sunucu hatası");
        errorResponse.put("message", "Bir hata oluştu. Lütfen daha sonra tekrar deneyin.");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 