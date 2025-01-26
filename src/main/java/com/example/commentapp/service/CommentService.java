package com.example.commentapp.service;

import com.example.commentapp.dto.CommentUpdateRequestDto;
import com.example.commentapp.dto.CommentViewRequestDto;
import com.example.commentapp.dto.CommentViewResponseDto;
import com.example.commentapp.entity.Comment;
import com.example.commentapp.entity.Users;
import com.example.commentapp.enums.CommentState;
import com.example.commentapp.enums.UserRole;
import com.example.commentapp.exception.CustomConflictException;
import com.example.commentapp.repository.CommentRepository;
import com.example.commentapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CommentService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private SecurityContext context;

    public CommentViewResponseDto getAll(CommentViewRequestDto searchComment) {
        Authentication authentication = context != null ? context.getAuthentication() : SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(UserRole.ROLE_ADMIN.toString()))) {
            List<Comment> comments = commentRepository.findByDeletedFalse();
            return new CommentViewResponseDto(comments.size(), comments) ;
        }else {
            List<Comment> comments = commentRepository.findByCreatedByAndDeletedFalse(authentication.getName());
            return new CommentViewResponseDto(comments.size(), comments) ;
        }
    }

    public Comment createUpdate(CommentUpdateRequestDto dto) throws CustomConflictException {
        Authentication authentication = context != null ? context.getAuthentication() : SecurityContextHolder.getContext().getAuthentication();
        Comment comment = null;
        if (dto.getId() != null) {
            if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(UserRole.ROLE_ADMIN.toString()))) {
                comment = commentRepository.findByUuid(dto.getId());
            }else {
                comment = commentRepository.findByUuidAndCreatedBy(dto.getId(), authentication.getName());
            }

            if (comment == null)
                throw new CustomConflictException("Comment not found with id: " + dto.getId());

            if (comment.isDeleted())
                throw new CustomConflictException("Comment already deleted state: " + dto.getId());

        } else {
            comment = new Comment();
            comment.setCreatedDate(System.currentTimeMillis());
            comment.setCreatedBy(authentication.getName());
            comment.setState(CommentState.PUBLISHED);
            comment.setDeleted(false);
        }

        comment.setComment(dto.getText());
        comment.setAnonymous(dto.isAnonymous());
        comment.setLastModifiedBy(authentication.getName());
        comment.setLastModifiedDate(System.currentTimeMillis());
        commentRepository.save(comment);
        return comment;
    }

    public Comment deleteComment(UUID uuid) throws CustomConflictException {
        Authentication authentication = context != null ? context.getAuthentication() : SecurityContextHolder.getContext().getAuthentication();
        Comment comment = null;
        if (authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(UserRole.ROLE_ADMIN.toString()))) {
            comment = commentRepository.findByUuid(uuid);
        }else {
            comment = commentRepository.findByUuidAndCreatedBy(uuid, authentication.getName());
        }

        if (comment == null)
            throw new CustomConflictException("Comment not found with id: " + uuid);

        if (comment.isDeleted())
            throw new CustomConflictException("Comment already deleted state: " + uuid);

        comment.setDeleted(true);
        comment.setState(CommentState.DELETED);
        comment.setLastModifiedBy(authentication.getName());
        comment.setLastModifiedDate(System.currentTimeMillis());
        commentRepository.save(comment);
        return comment;
    }


    // Load initial data
    @PostConstruct
    public void init() {

        userRepository.save(new Users(UUID.randomUUID(), "user1",  passwordEncoder.encode("user1"), Set.of(UserRole.ROLE_USER.toString())));
        userRepository.save(new Users(UUID.randomUUID(), "user2",  passwordEncoder.encode("user2"), Set.of(UserRole.ROLE_USER.toString())));
        userRepository.save(new Users(UUID.randomUUID(), "admin",  passwordEncoder.encode("admin"), Set.of(UserRole.ROLE_ADMIN.toString())));
        userRepository.save(new Users(UUID.randomUUID(), "guest",  passwordEncoder.encode("guest"), Set.of(UserRole.ROLE_GUEST.toString())));

        String user1 = userRepository.findByUsername("user1").get().getUsername();
        String user2 = userRepository.findByUsername("user2").get().getUsername();

        commentRepository.save(
                new Comment(UUID.randomUUID(), "This is the first comment",
                        CommentState.PUBLISHED, System.currentTimeMillis(), System.currentTimeMillis(),
                        false, false, user1,user1));

        commentRepository.save(
                new Comment(UUID.randomUUID(), "This is the second comment",
                        CommentState.PUBLISHED, System.currentTimeMillis(), System.currentTimeMillis(),
                        false, false, user1,user1));

        commentRepository.save(
                new Comment(UUID.randomUUID(), "This is the third comment",
                        CommentState.PUBLISHED, System.currentTimeMillis(), System.currentTimeMillis(),
                        false, false, user1,user1));

        commentRepository.save(
                new Comment(UUID.randomUUID(), "This is the fourth comment",
                        CommentState.PUBLISHED, System.currentTimeMillis(), System.currentTimeMillis(),
                        false, false, user2,user2));

        commentRepository.save(
                new Comment(UUID.randomUUID(), "This is the fifth comment",
                        CommentState.PUBLISHED, System.currentTimeMillis(), System.currentTimeMillis(),
                        false, false, user2,user2));

        commentRepository.save(
                new Comment(UUID.randomUUID(), "This is the sixth comment",
                        CommentState.PUBLISHED, System.currentTimeMillis(), System.currentTimeMillis(),
                        false, false, user2,user2));
    }
}
