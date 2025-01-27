package com.example.commentapp.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
public class HttpStatusHelper {

    public static ResponseEntity<Object> success(Object object) {
        Map<String, Object> map = new HashMap<>();
        map.put("response", object);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public static ResponseEntity<Object> successlist(Object object, int size) {
        Map<String, Object> map = new HashMap<>();
        map.put("response", object);
        map.put("total", size);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public static ResponseEntity<Object> error(Exception exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", exception.getMessage());
        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
