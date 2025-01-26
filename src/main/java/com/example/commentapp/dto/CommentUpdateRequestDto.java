package com.example.commentapp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateRequestDto {
    private UUID id;
    private String text;
    private boolean anonymous;
}
