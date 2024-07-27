package com.luckvicky.blur.domain.comment.repository;

import com.luckvicky.blur.domain.board.model.entity.Board;
import com.luckvicky.blur.domain.comment.exception.NotExistCommentException;
import com.luckvicky.blur.domain.comment.model.entity.Comment;
import com.luckvicky.blur.domain.comment.model.entity.CommentType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    default Comment getOrThrow(UUID id) {
        return findById(id).orElseThrow(NotExistCommentException::new);
    }

    @Query("SELECT c "
            + "FROM Comment c "
            + "LEFT JOIN FETCH c.replies "
            + "LEFT JOIN FETCH c.member "
            + "WHERE c.board.id = :boardId AND c.type = :type")
    List<Comment> findAllByBoardAndType(
            @Param("boardId") UUID boardId,
            @Param("type") CommentType type
    );

    Optional<Comment> findByIdAndBoard(UUID commentId, Board board);

}
