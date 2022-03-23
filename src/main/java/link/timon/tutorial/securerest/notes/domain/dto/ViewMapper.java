package link.timon.tutorial.securerest.notes.domain.dto;

import java.util.List;
import link.timon.tutorial.securerest.notes.domain.Note;
import link.timon.tutorial.securerest.notes.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for User -> UserView and reverse.
 *
 * @author Timon Link
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
)
public interface ViewMapper {

    ViewMapper INSTANCE = Mappers.getMapper(ViewMapper.class);

    /**
     * Maps given User to UserView.
     *
     * @param user The User to Map.
     *
     * @return The createdUserview.
     */
    UserView userToView(User user);

    /**
     * Maps given UserView to User.
     *
     * @param userView The UserView to map.
     *
     * @return The mapped User.
     */
    User userViewToModel(UserView userView);

    /**
     * Maps given Note to NoteView.
     *
     * @param note The Note to map.
     *
     * @return The created NoteView.
     */
    NoteView noteToView(Note note);

    /**
     * Maps given List of Notes to a List of NoteViews.
     *
     * @param notes The List of Notes to map.
     *
     * @return The mapped NoteViews.
     */
    List<NoteView> notesToViews(List<Note> notes);

    /**
     * Maps a given NoteView to Note.
     *
     * @param noteView The NoteView to map.
     *
     * @return The created Note.
     */
    Note noteViewToModel(NoteView noteView);

    /**
     * Maps given List of NoteViews to List of Notes.
     *
     * @param noteViews The NoteViews to map.
     *
     * @return The mapped Notes.
     */
    List<Note> noteViewsToModels(List<NoteView> noteViews);

}
