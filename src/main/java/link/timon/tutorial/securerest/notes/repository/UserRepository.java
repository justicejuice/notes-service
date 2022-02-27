package link.timon.tutorial.securerest.notes.repository;

import java.util.Optional;
import link.timon.tutorial.securerest.notes.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for User data access.
 * 
 * @author Timon
 */
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Searches for an user by given email;
     * 
     * @param email The email to search by.
     * 
     * @return The found user or <code>empty</code>.
     */
    Optional<User> findByEmail(String email);

}
