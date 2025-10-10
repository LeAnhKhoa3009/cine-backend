package com.cine.cinemovieservice.validator;

import org.springframework.stereotype.Component;

@Component
public class RoomValidator {

    public void validateRoom(String row, Integer col) {

        if (row == null || row.isBlank()) {
            throw new IllegalArgumentException("Row cannot be null or empty");
        }

        if (col == null) {
            throw new IllegalArgumentException("Column cannot be null");
        }
        int rowNumber = convertRowToNumber(row);
        if (rowNumber > 15) {
            throw new IllegalArgumentException("Row cannot exceed 15 (A to O)");
        }

        if (col > 15) {
            throw new IllegalArgumentException("Column cannot exceed 15");
        }
    }

    private int convertRowToNumber(String row) {
        row = row.trim().toUpperCase();
        if (row.length() == 1 && row.charAt(0) >= 'A' && row.charAt(0) <= 'O') {
            return row.charAt(0) - 'A' + 1;
        }
        throw new IllegalArgumentException("Invalid row format. Must be between A and O");
    }
}