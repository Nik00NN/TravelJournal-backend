package travel.journal.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.journal.api.dto.CreateNoteDTO;
import travel.journal.api.dto.travelJournal.outbound.NoteDetailsDTO;
import travel.journal.api.service.NoteServiceImpl;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/travel-journal")
public class NoteController {

    private final NoteServiceImpl noteService;

    public NoteController(NoteServiceImpl noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/saveNote/{traveljournalid}")
    public ResponseEntity<?> saveNote(@PathVariable("traveljournalid") Integer id, @Valid @RequestPart("CreateNoteDTO") CreateNoteDTO createNoteDTO, @RequestParam("files") List<MultipartFile> files) throws IOException {
        noteService.save(id, createNoteDTO, files);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/deleteNote/{id}")
    ResponseEntity<Void> deleteNote(@PathVariable("id") int noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/travel/{travelId}/view-note/{noteId}")
    public NoteDetailsDTO getNoteDetails(@PathVariable int travelId, @PathVariable int noteId) {
        return noteService.getNoteDetails(travelId, noteId);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/travels/{travelId}/notes/{noteId}")
    public ResponseEntity<?> editNote(@PathVariable("travelId") Integer id, @PathVariable("noteId") Integer noteId, @Valid @RequestPart("CreateNoteDTO") CreateNoteDTO createNoteDTO, @RequestParam("files") List<MultipartFile> files) throws IOException {
        noteService.editNote(id, noteId, createNoteDTO, files);
        return ResponseEntity.ok().build();
    }
}
