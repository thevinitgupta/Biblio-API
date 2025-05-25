package tech.biblio.BookListing.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.biblio.BookListing.entities.Comment;

import java.util.List;

@Document("comment")
@Repository
public interface CommentRepository extends MongoRepository<Comment, ObjectId> {

    // fetch all base comments paginated
    List<Comment> findAllByPostId(String postId, Pageable pageable);

    // fetch all replies to parentId
    List<Comment> findAllByParentIdOrderByCreatedAtAsc(String parentId);

    // Count comments for a post
    long countByPostId(String postId);
}
