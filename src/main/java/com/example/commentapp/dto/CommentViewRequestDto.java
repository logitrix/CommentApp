package com.example.commentapp.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentViewRequestDto {
    private String searchText;
    private String searchByUser;
    private String sortBy;
    private String orderedBy;
    private String page;
    private String size;
}
