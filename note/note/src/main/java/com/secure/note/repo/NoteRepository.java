package com.secure.note.repo;

// NoteRepository.java


//import com.secure.notes.models.Note;
import com.secure.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerUsername(String ownerUsername);
}
