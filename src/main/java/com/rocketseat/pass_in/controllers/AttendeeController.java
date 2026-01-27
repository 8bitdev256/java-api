package com.rocketseat.pass_in.controllers;

import com.rocketseat.pass_in.domain.attendee.Attendee;
import com.rocketseat.pass_in.dto.attendee.AttendeeBadgeResponseDTO;
import com.rocketseat.pass_in.services.AttendeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/attendees")
@RequiredArgsConstructor
public class AttendeeController {

    private final AttendeeService attendeeService;

    @GetMapping
    public List<Attendee> getAllAttendees() {
        return this.attendeeService.getAllAttendees();
    }

    @DeleteMapping("/{attendeeId}")
    public void deleteAttendeeById(@PathVariable Integer attendeeId) {
        this.attendeeService.deleteAttendeeById(attendeeId);
    }

    @GetMapping("/{attendeeId}/badge")
    public ResponseEntity<AttendeeBadgeResponseDTO> getAttendeeBadge(@PathVariable Integer attendeeId, UriComponentsBuilder uriComponentsBuilder) {
        AttendeeBadgeResponseDTO response = this.attendeeService.getAttendeeBadge(attendeeId, uriComponentsBuilder);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{attendeeId}/check-in")
    public ResponseEntity registerCheckIn(@PathVariable Integer attendeeId, UriComponentsBuilder uriComponentsBuilder) {
        this.attendeeService.checkInAttendee(attendeeId);

        var uri = uriComponentsBuilder.path("/attendees/{attendeeId}/badge").buildAndExpand(attendeeId).toUri();

        return ResponseEntity.created(uri).build();
    }

}
