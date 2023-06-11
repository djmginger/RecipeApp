package com.redheaddev.springframework.converters;

import com.redheaddev.springframework.commands.NotesCommand;
import com.redheaddev.springframework.domain.Notes;
import jakarta.annotation.Nullable;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NotesToNotesCommand implements Converter<Notes, NotesCommand> {

    @Synchronized
    @Nullable
    @Override
    public NotesCommand convert(Notes source) {
        if (source == null) {
            return null;
        }

        final NotesCommand notesCommand = new NotesCommand();
        notesCommand.setId(source.getId());
        notesCommand.setRecipe(source.getRecipe());
        notesCommand.setRecipeNotes(source.getRecipeNotes());
        return notesCommand;
    }
}
