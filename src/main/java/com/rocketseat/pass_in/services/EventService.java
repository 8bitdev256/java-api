package com.rocketseat.pass_in.services;

import com.rocketseat.pass_in.domain.attendee.Attendee;
import com.rocketseat.pass_in.domain.attendee.exceptions.EventFullException;
import com.rocketseat.pass_in.domain.event.Event;
import com.rocketseat.pass_in.domain.event.exceptions.EventNotFoundException;
import com.rocketseat.pass_in.dto.attendee.AttendeeIdDTO;
import com.rocketseat.pass_in.dto.attendee.AttendeeRequestDTO;
import com.rocketseat.pass_in.dto.event.EventIdDTO;
import com.rocketseat.pass_in.dto.event.EventRequestDTO;
import com.rocketseat.pass_in.dto.event.EventResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.rocketseat.pass_in.repositories.EventRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId) {
        Event event = this.getEventById(eventId);
        int attendeesQty = this.attendeeService.getAttendeesQtyFromEvent(eventId);
        return new EventResponseDTO(event, attendeesQty);
    }

    public EventIdDTO createEvent(EventRequestDTO eventDTO) {
        Event newEvent  = new Event();
        newEvent.setTitle(eventDTO.title());
        newEvent.setDetails(eventDTO.details());
        newEvent.setMaximumAttendees(eventDTO.maximumAttendees());
        newEvent.setSlug(this.createSlug(eventDTO.title()));

        this.eventRepository.save(newEvent);

        return new EventIdDTO(newEvent.getId());
    }

    public AttendeeIdDTO registerAttendeeOnEvent(String eventId, AttendeeRequestDTO attendeeRequestDTO) {
        this.attendeeService.verifyAttendeeSubscription(eventId, attendeeRequestDTO.email());
        Event event = getEventById(eventId);
        int attendeesQty = this.attendeeService.getAttendeesQtyFromEvent(eventId);

        if (event.getMaximumAttendees() <= attendeesQty) throw new EventFullException("Event is full");

        Attendee newAttendee = new Attendee();
        newAttendee.setName(attendeeRequestDTO.name());
        newAttendee.setEmail(attendeeRequestDTO.email());
        newAttendee.setEvent(event);
        newAttendee.setCreatedAt(LocalDateTime.now());
        this.attendeeService.registerAttendee(newAttendee);

        return new AttendeeIdDTO(newAttendee.getId());
    }

    private Event getEventById(String eventId) {
        return this.eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event not found with ID:" + eventId));
    }

    private String createSlug(String text) {
        //Código abaixo é uma decomposição canônica
        //Basicamente serve para transformar caracteres com acento ('é', por exemplo)
        //em dois caracteres, sendo o acento o último ('e´', por exemplo);

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD); //Efetuada decomposição canônica (conforme mencionado acima. Transforma 'é' em 'e´')
        return normalized.replaceAll("[\\p{InCOMBINING_DIACRITICAL_MARKS}]", "") //Remove acentos da string normalizada ('e´' vira apenas 'e');
                .replaceAll("[^\\w\\s]", "") //Remove tudo o que não for letra e número da string;
                .replaceAll("[\\s+]", "-") //Remove espaços em branco por hífen. Exemplo 'Sao Paulo' vira 'Sao-Paulo' e 'Sao   Paulo' vira 'Sao-Paulo'
                .toLowerCase();
    }
}
