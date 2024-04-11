package travel.journal.api.dto.travelJournal.outbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.journal.api.entities.Files;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDetailsDTO {
    private Integer travelId;
    private Integer noteId;
    private List<Files> filesList;
    private String destinationName;
    private LocalDate date;
    private String description;
}
