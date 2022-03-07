package link.timon.tutorial.securerest.notes.domain.dto;

import link.timon.tutorial.securerest.notes.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for User -> UserView and reverse.
 *
 * @author Timon Link
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserViewMapper {

    UserViewMapper INSTANCE = Mappers.getMapper(UserViewMapper.class);

    /**
     * Maps given User to UserView.
     *
     * @param user The User to Map.
     *
     * @return The createdUserview.
     */
    UserView modelToView(User user);

    /**
     * Maps given UserView to User.
     *
     * @param userView The UserView to map.
     *
     * @return The mapped User.
     */
    User viewToModel(UserView userView);

}
