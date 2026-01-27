package com.rocketseat.pass_in.controllers;

import com.rocketseat.pass_in.domain.event.Event;
import com.rocketseat.pass_in.dto.attendee.AttendeeIdDTO;
import com.rocketseat.pass_in.dto.attendee.AttendeeRequestDTO;
import com.rocketseat.pass_in.dto.attendee.AttendeesListResponseDTO;
import com.rocketseat.pass_in.dto.event.EventIdDTO;
import com.rocketseat.pass_in.dto.event.EventRequestDTO;
import com.rocketseat.pass_in.dto.event.EventResponseDTO;
import com.rocketseat.pass_in.services.AttendeeService;
import com.rocketseat.pass_in.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final AttendeeService attendeeService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = this.eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @DeleteMapping("/{id}")
    public void deleteEventById(@PathVariable String id) {
        this.eventService.deleteEventById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable String id) {
        EventResponseDTO event = this.eventService.getEventDetail(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventIdDTO> createEvent(@RequestBody EventRequestDTO body, UriComponentsBuilder uriComponentsBuilder) {
        EventIdDTO eventIdDTO = this.eventService.createEvent(body);

        var uri = uriComponentsBuilder.path("/events/{id}").buildAndExpand(eventIdDTO.eventId()).toUri();

        return ResponseEntity.created(uri).body(eventIdDTO);
    }

    @GetMapping("/attendees/{id}")
    public ResponseEntity<AttendeesListResponseDTO> getEventAttendees(@PathVariable String id,
                                                                      @RequestParam(value = "query", defaultValue = "", required = false) String query,
                                                                      @RequestParam(value = "pageIndex", defaultValue = "0", required = false) int pageIndex,
                                                                      @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        AttendeesListResponseDTO attendeesListResponse = this.attendeeService.getEventsAttendee(id, pageIndex, pageSize, query);
        return ResponseEntity.ok(attendeesListResponse);
    }

    @PostMapping("/{eventId}/attendees")
    public ResponseEntity<AttendeeIdDTO> registerParticipant(@PathVariable String eventId, @RequestBody AttendeeRequestDTO body, UriComponentsBuilder uriComponentsBuilder) {
        AttendeeIdDTO attendeeIdDTO = this.eventService.registerAttendeeOnEvent(eventId, body);

        var uri = uriComponentsBuilder.path("/attendees/{attendeeId}/badge").buildAndExpand(attendeeIdDTO.attendeeId()).toUri();

        return ResponseEntity.created(uri).body(attendeeIdDTO);
    }

}
