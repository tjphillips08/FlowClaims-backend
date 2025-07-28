package com.flowclaims.backend.controller;

import com.flowclaims.backend.model.Claim;
import com.flowclaims.backend.model.Note;
import com.flowclaims.backend.repository.ClaimRepository;
import com.flowclaims.backend.repository.NoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final ClaimRepository claimRepository;

    @Autowired
    public NoteController(NoteRepository noteRepository, ClaimRepository claimRepository) {
        this.noteRepository = noteRepository;
        this.claimRepository = claimRepository;
    }

    // Add a note to a claim
    @PostMapping("/add-to-claim/{claimId}")
    public ResponseEntity<Note> addNoteToClaim(@PathVariable Long claimId, @RequestBody Note noteRequest) {
        Optional<Claim> optionalClaim = claimRepository.findById(claimId);

        if (optionalClaim.isPresent()) {
            Claim claim = optionalClaim.get();
            noteRequest.setClaim(claim);
            noteRequest.setCreatedAt(LocalDateTime.now());
            Note savedNote = noteRepository.save(noteRequest);
            return ResponseEntity.ok(savedNote);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all notes for a claim
    @GetMapping("/claim/{claimId}")
    public ResponseEntity<List<Note>> getNotesForClaim(@PathVariable Long claimId) {
        Optional<Claim> optionalClaim = claimRepository.findById(claimId);
        if (optionalClaim.isPresent()) {
            List<Note> notes = optionalClaim.get().getNotes();
            return ResponseEntity.ok(notes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // (Optional) Get note by id
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return noteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // (Optional) Delete note by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
