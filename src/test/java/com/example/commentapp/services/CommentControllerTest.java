package com.example.commentapp.services;

import com.example.commentapp.dto.CommentUpdateRequestDto;
import com.example.commentapp.dto.CommentViewRequestDto;
import com.example.commentapp.dto.CommentViewResponseDto;
import com.example.commentapp.entity.Comment;
import com.example.commentapp.exception.CustomConflictException;
import com.example.commentapp.repository.CommentRepository;
import com.example.commentapp.repository.UserRepository;
import com.example.commentapp.service.CommentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class CommentControllerTest {

    private static CommentService commentService;

    private static UserRepository userRepository;

    private static CommentRepository commentRepository;

    private static SecurityContext context;

    private static UUID uuidMock;

    private static Comment commentMock;

    private static Comment commentDeletedMock;

    private static List<Comment> user1CommentsMock = new ArrayList<>();

    private static List<Comment> user2CommentsMock = new ArrayList<>();

    private static List<Comment> allCommentsMock = new ArrayList<>();

    private static Authentication adminAuthMock;

    private static Authentication userAuthMock;

    private static Authentication userAuthMock2;

    @BeforeAll
    public static void initialize() {
        commentService = new CommentService();
        userRepository = Mockito.mock(UserRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        context = Mockito.mock(SecurityContext.class);
        ReflectionTestUtils.setField(commentService, "userRepository", userRepository);
        ReflectionTestUtils.setField(commentService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(commentService, "context", context);

        uuidMock = UUID.fromString("29d4bb18-fddc-497b-8115-50e29023d4a8");
        commentMock = new Comment(uuidMock, "Lorem ipsum dolor sit amet", "user1");
        commentDeletedMock = new Comment(uuidMock, "Lorem ipsum dolor sit amet", "user1");;
        commentDeletedMock.setDeleted(true);

        UserDetails adminDetails = User.builder().username("admin").password("admin").authorities("ROLE_ADMIN").build();
        adminAuthMock = new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());

        UserDetails userDetails = User.builder().username("user1").password("user1").authorities("ROLE_USER").build();
        userAuthMock = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        UserDetails userDetails2 = User.builder().username("user2").password("user2").authorities("ROLE_USER").build();
        userAuthMock2 = new UsernamePasswordAuthenticationToken(userDetails2, null, userDetails2.getAuthorities());

        user1CommentsMock.add(new Comment(uuidMock, "This is the 1st comment", "user1"));
        user1CommentsMock.add(new Comment(uuidMock, "This is the 2nd comment", "user1"));
        user1CommentsMock.add(new Comment(uuidMock, "This is the 3rd comment", "user1"));
        user2CommentsMock.add(new Comment(uuidMock, "This is the 4th comment", "user2"));
        user2CommentsMock.add(new Comment(uuidMock, "This is the 5th comment", "user2"));
        user2CommentsMock.add(new Comment(uuidMock, "This is the 6th comment", "user2"));
        allCommentsMock.addAll(user1CommentsMock);
        allCommentsMock.addAll(user2CommentsMock);
    }

    @Test
    public void createUpdate_updateUserCommentByadmin() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(uuidMock, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(adminAuthMock);
        when(commentRepository.findByUuid(uuidMock)).thenReturn(commentMock);

        // success
        Comment updatedComment = commentService.createUpdate(dto);
        assertNotNull(updatedComment);
        assertEquals(updatedComment.getComment(), commentTextupdate);
        verify(commentRepository, times(1)).findByUuid(uuidMock);

    }

    @Test
    public void createUpdate_updateUserCommentBySameUser() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(uuidMock, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(userAuthMock);
        when(commentRepository.findByUuidAndCreatedBy(uuidMock, "user1")).thenReturn(commentMock);

        // success
        Comment updatedComment = commentService.createUpdate(dto);
        assertNotNull(updatedComment);
        assertEquals(updatedComment.getComment(), commentTextupdate);
        verify(commentRepository, times(1)).findByUuidAndCreatedBy(uuidMock, "user1");

    }

    @Test
    public void createUpdate_updateUserCommentBySameUser_butAlreadyDeleted() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(uuidMock, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(userAuthMock);
        when(commentRepository.findByUuidAndCreatedBy(uuidMock, "user1")).thenReturn(commentDeletedMock);

        // failed
        assertThrows(CustomConflictException.class, () -> commentService.createUpdate(dto));
        verify(commentRepository, times(2)).findByUuidAndCreatedBy(uuidMock, "user1");

    }

    @Test
    public void createUpdate_updateUserCommentByDiffUser() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(uuidMock, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(userAuthMock2);
        when(commentRepository.findByUuidAndCreatedBy(uuidMock, "user2")).thenReturn(null);

        // failed
        assertThrows(CustomConflictException.class, () -> commentService.createUpdate(dto));
        verify(commentRepository, times(1)).findByUuidAndCreatedBy(uuidMock, "user2");

    }

    @Test
    public void createUpdate_createNewComment() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(null, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(userAuthMock2);
        when(commentRepository.findByUuidAndCreatedBy(uuidMock, "user2")).thenReturn(null);

        // success
        Comment updatedComment = commentService.createUpdate(dto);
        assertNotNull(updatedComment);

    }

    @Test
    public void deleteComment_asAdmin() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(null, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(adminAuthMock);
        when(commentRepository.findByUuid(uuidMock)).thenReturn(commentMock);

        // success
        Comment deletedComment = commentService.deleteComment(uuidMock);
        assertNotNull(deletedComment);
        assertEquals(deletedComment.isDeleted(), true);
        verify(commentRepository, times(2)).findByUuid(uuidMock);

    }

    @Test
    public void deleteComment_asDiffUser() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(null, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(userAuthMock2);
        when(commentRepository.findByUuidAndCreatedBy(uuidMock, "user2")).thenReturn(null);

        // failed
        assertThrows(CustomConflictException.class, () -> commentService.deleteComment(uuidMock));
        verify(commentRepository, times(2)).findByUuidAndCreatedBy(uuidMock, "user2");

    }

    @Test
    public void deleteComment_asUserOwnComment_alreadyDeleted() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(null, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(userAuthMock);
        when(commentRepository.findByUuidAndCreatedBy(uuidMock, "user1")).thenReturn(commentMock);

        // success
        assertThrows(CustomConflictException.class, () -> commentService.deleteComment(uuidMock));
        verify(commentRepository, times(3)).findByUuidAndCreatedBy(uuidMock, "user1");

    }


    @Test
    public void getAll_asAdmin() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(null, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(adminAuthMock);
        when(commentRepository.findByDeletedFalse()).thenReturn(allCommentsMock);

        // success
        CommentViewResponseDto response = commentService.getAll(null);
        assertNotNull(response);
        assertEquals(6, response.getTotal());
        verify(commentRepository, times(1)).findByDeletedFalse();

    }

    @Test
    public void getAll_asUser() throws Exception {

        // Request Mock
        String commentTextupdate = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        CommentUpdateRequestDto dto = new CommentUpdateRequestDto(null, commentTextupdate, false);

        when(context.getAuthentication()).thenReturn(userAuthMock);
        when(commentRepository.findByCreatedByAndDeletedFalse(any())).thenReturn(user1CommentsMock);

        // success
        CommentViewResponseDto response = commentService.getAll(null);
        assertNotNull(response);
        assertEquals(response.getTotal(), 3);
        verify(commentRepository, times(1)).findByCreatedByAndDeletedFalse(any());

    }
}
