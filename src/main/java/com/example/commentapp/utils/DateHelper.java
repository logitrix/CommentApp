package com.example.commentapp.utils;

import com.example.commentapp.exception.CustomConflictException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    public static Date stringToDate(String date) throws CustomConflictException {
        try {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
        } catch (ParseException e) {
            throw new CustomConflictException("Invalid date format. Expected format: yyyy-MM-dd");
        }
    }
}
