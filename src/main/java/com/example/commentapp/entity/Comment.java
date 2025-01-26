package com.example.commentapp.entity;

import com.example.commentapp.enums.CommentState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @JdbcTypeCode(Types.VARCHAR)
    @Column(length = 36)
    private UUID uuid;

    private String comment;

    private CommentState state;

    private long createdDate;

    private long lastModifiedDate;

    private boolean deleted;

    private boolean anonymous;

    private String createdBy;

    private String lastModifiedBy;

    public Comment(UUID uuid, String comment, String createdBy) {
        this.uuid = uuid;
        this.comment = comment;
        this.createdBy = createdBy;
    }

}
