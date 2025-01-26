package com.example.commentapp.controller;

import com.example.commentapp.dto.CommentUpdateRequestDto;
import com.example.commentapp.dto.CommentViewRequestDto;
import com.example.commentapp.entity.Comment;
import com.example.commentapp.exception.CustomConflictException;
import com.example.commentapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/view")
    @PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getAll(/*@RequestBody CommentViewRequestDto searchComment*/) {
        return ResponseEntity.ok(commentService.getAll(null));
    }

    @PostMapping("/create-update")
    @PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> createUpdate(@RequestBody CommentUpdateRequestDto dto) throws CustomConflictException {
        try {
            return ResponseEntity.ok(commentService.createUpdate(dto));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{uuid}")
    @PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_ADMIN') ")
    public ResponseEntity<Object> deleteComment(@PathVariable UUID uuid) {
        try {
            commentService.deleteComment(uuid);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
