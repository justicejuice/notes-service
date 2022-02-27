package link.timon.tutorial.securerest.notes.repository;

import java.util.Collection;
import link.timon.tutorial.securerest.notes.domain.Note;
import link.timon.tutorial.securerest.notes.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for notes.
 *
 * @author Timon
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    Collection<Note> findByAuthor(User author);

}
