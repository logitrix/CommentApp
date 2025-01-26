package com.example.commentapp.dto;


import com.example.commentapp.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentViewResponseDto {
    private int total;
    private List<Comment> comments;
}
