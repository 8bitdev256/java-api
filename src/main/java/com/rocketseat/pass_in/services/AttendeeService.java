package com.rocketseat.pass_in.services;

import com.rocketseat.pass_in.domain.attendee.Attendee;
import com.rocketseat.pass_in.domain.attendee.exceptions.AttendeeAlreadyExistsException;
import com.rocketseat.pass_in.domain.attendee.exceptions.AttendeeNotFoundException;
import com.rocketseat.pass_in.domain.checkin.CheckIn;
import com.rocketseat.pass_in.dto.attendee.AttendeeBadgeResponseDTO;
import com.rocketseat.pass_in.dto.attendee.AttendeeDetails;
import com.rocketseat.pass_in.dto.attendee.AttendeesListResponseDTO;
import com.rocketseat.pass_in.dto.attendee.AttendeeBadgeDTO;
import com.rocketseat.pass_in.repositories.AttendeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {
    private final AttendeeRepository attendeeRepository; //sem o final o Spring não identifica como um required arg esta variável
    private final CheckInService checkInService;

    public int getAttendeesQtyFromEvent(String eventId) {
        return this.attendeeRepository.findByEventId(eventId).size();
    }

    public int getAttendeesQtyFromEvent(String eventId, String query) {
        List<Attendee> attendeeList = this.attendeeRepository.findByEventId(eventId);

        return getAttendeesFiltered(attendeeList, query).size();
    }

    public List<Attendee> getAttendeesFiltered(List<Attendee> attendeeList, String query) {
        List<Attendee> attendeeListFiltered = attendeeList;

        if (!Objects.equals(query, "")) {
            attendeeListFiltered = attendeeListFiltered.stream().filter(attendee -> attendee.getName().toLowerCase().contains(query.toLowerCase())).toList();
        }

        return attendeeListFiltered;
    }

    public List<Attendee> getAllAttendees() {
        return this.attendeeRepository.findAll();
    }

    public void deleteAttendeeById(Integer attendeeId) {
        this.checkInService.deleteCheckInByAttendeeId(attendeeId);

        Optional<Attendee> attendee = this.attendeeRepository.findById(attendeeId);

        if (attendee.isPresent())
            this.attendeeRepository.deleteById(attendeeId);
    }

    public List<Attendee> getAllAttendeesFromEvent(String eventId) {
        return this.attendeeRepository.findByEventId(eventId);
    }

    public List<Attendee> getAllAttendeesFromEvent(String eventId, int pageIndex, int pageSize) {
        return this.attendeeRepository.findByEventId(eventId, PageRequest.of(pageIndex, pageSize, Sort.by("createdAt").descending()));
    }

    public AttendeesListResponseDTO getEventsAttendee(String eventId, int pageIndex, int pageSize, String query) {
        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId, pageIndex, pageSize);

        attendeeList = getAttendeesFiltered(attendeeList, query);

        List<AttendeeDetails> attendeeDetailsList = attendeeList.stream().map(attendee -> {
            Optional<CheckIn> checkIn = this.checkInService.getCheckInByAttendeeId(attendee.getId());
            LocalDateTime checkedInAt = checkIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);
            return new AttendeeDetails(attendee.getId(), attendee.getName(), attendee.getEmail(), attendee.getCreatedAt(), checkedInAt);
        }).toList();

        int total = this.getAttendeesQtyFromEvent(eventId, query);

        return new AttendeesListResponseDTO(attendeeDetailsList, total);
    }

    public void verifyAttendeeSubscription(String eventId, String email) {
        Optional<Attendee> isAttendeeRegistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);

        if (isAttendeeRegistered.isPresent()) throw new AttendeeAlreadyExistsException("Attendee is already registered");
    }

    public Attendee registerAttendee(Attendee newAttendee) {
        this.attendeeRepository.save(newAttendee);

        return newAttendee;
    }

    public void checkInAttendee(Integer attendeeId) {
        Attendee attendee = this.getAttendee(attendeeId);
        this.checkInService.registerCheckIn(attendee);
    }

    private Attendee getAttendee(Integer attendeeId) {
        return this.attendeeRepository.findById(attendeeId).orElseThrow(() -> new AttendeeNotFoundException(("Attendee not found with ID: " + attendeeId)));
    }

    private void deleteAttendee(Integer attendeeId) {
        Optional<Attendee> attendee = this.attendeeRepository.findById(attendeeId);

        if (attendee.isPresent()) {
            this.checkInService.deleteCheckInByAttendeeId(attendeeId);
            this.attendeeRepository.deleteById(attendeeId);
        }
    }

    public AttendeeBadgeResponseDTO getAttendeeBadge(Integer attendeeId, UriComponentsBuilder uriComponentsBuilder) {
        Attendee attendee = this.getAttendee(attendeeId);

        var uri = uriComponentsBuilder.path("/attendees/{id}/check-in").buildAndExpand(attendeeId).toUri().toString();

        AttendeeBadgeDTO attendeeBadgeDTO = new AttendeeBadgeDTO(attendee.getId(), attendee.getName(), attendee.getEmail(), uri, attendee.getEvent().getTitle());
        return new AttendeeBadgeResponseDTO(attendeeBadgeDTO);
    }

}
