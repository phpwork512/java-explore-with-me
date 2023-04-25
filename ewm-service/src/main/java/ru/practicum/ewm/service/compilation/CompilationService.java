package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.common.pagination.PaginationCalculator;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.model.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.model.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.event.EventRepository;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.exceptions.CompilationNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public Compilation getCompilationById(long compilationId) {
        Optional<Compilation> optionalCompilation = compilationRepository.findById(compilationId);
        if (optionalCompilation.isEmpty()) {
            throw new CompilationNotFoundException("Компиляция " + compilationId + " не найдена");
        } else {
            return optionalCompilation.get();
        }
    }

    public List<Compilation> getAllCompilations(Boolean pinned, int from, int size) {
        Pageable page = PaginationCalculator.getPage(from, size);
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned, page);
        } else {
            return compilationRepository.findAll(page).getContent();
        }
    }

    public Compilation create(NewCompilationDto newCompilationDto) {
        Compilation compilation = Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .build();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(Set.copyOf(eventRepository.findByIdIn(List.copyOf(newCompilationDto.getEvents()))));
        }

        return compilationRepository.save(compilation);
    }

    public Compilation update(UpdateCompilationRequest updateRequest, long compilationId) {
        Compilation compilation = getCompilationById(compilationId);
        if (updateRequest.getEvents() != null) {
            if (updateRequest.getEvents().isEmpty()) {
                compilation.setEvents(new HashSet<>());
            } else {
                compilation.setEvents(Set.copyOf(eventRepository.findByIdIn(List.copyOf(updateRequest.getEvents()))));
            }
        }

        if (updateRequest.getPinned() != null) compilation.setPinned(updateRequest.getPinned());
        if (updateRequest.getTitle() != null) compilation.setTitle(updateRequest.getTitle());

        return compilationRepository.save(compilation);
    }

    public void delete(long compilationId) {
        compilationRepository.delete(getCompilationById(compilationId));
    }
}