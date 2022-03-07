package link.timon.tutorial.securerest.notes.domain.dto;

import java.util.List;
import link.timon.tutorial.securerest.notes.domain.Note;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Maps the Note to NoteView and reverse.
 *
 * @author Timon Link
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NoteViewMapper {

    NoteViewMapper INSTANCE = Mappers.getMapper(NoteViewMapper.class);

    /**
     * Maps given Note to NoteView.
     *
     * @param note The Note to map.
     *
     * @return The created NoteView.
     */
    NoteView modelToView(Note note);

    /**
     * Maps given List of Notes to a List of NoteViews.
     *
     * @param notes The List of Notes to map.
     *
     * @return The mapped NoteViews.
     */
    List<NoteView> modelsToViews(List<Note> notes);

    /**
     * Maps a given NoteView to Note.
     *
     * @param noteView The NoteView to map.
     *
     * @return The created Note.
     */
    Note viewToModel(NoteView noteView);

    /**
     * Maps given List of NoteViews to List of Notes.
     *
     * @param noteViews The NoteViews to map.
     *
     * @return The mapped Notes.
     */
    List<Note> viewsToModels(List<NoteView> noteViews);

}
