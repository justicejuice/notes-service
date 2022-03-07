package link.timon.tutorial.securerest.notes.domain.dto;

import link.timon.tutorial.securerest.notes.domain.User;
import org.mapstruct.Mapper;

/**
 * Mapper for User -> UserView and reverse.
 *
 * @author Timon Link
 */
@Mapper(componentModel = "spring")
public interface UserViewMapper {

    /**
     * Maps given User to UserView.
     *
     * @param user The User to Map.
     *
     * @return The createdUserview.
     */
    UserView map(User user);

    /**
     * Maps given UserView to User.
     *
     * @param userView The UserView to map.
     *
     * @return The mapped User.
     */
    User map(UserView userView);

}
