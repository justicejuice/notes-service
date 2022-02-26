package link.timon.tutorial.sercurerest.notes.repository;

import link.timon.tutorial.sercurerest.notes.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for User data access.
 * 
 * @author Timon
 */
public interface UserRepository extends MongoRepository<User, String> {
    
}
