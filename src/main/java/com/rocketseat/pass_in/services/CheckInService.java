package com.rocketseat.pass_in.services;

import com.rocketseat.pass_in.domain.attendee.Attendee;
import com.rocketseat.pass_in.domain.checkin.CheckIn;
import com.rocketseat.pass_in.domain.checkin.exceptions.CheckInAlreadyExistsException;
import com.rocketseat.pass_in.repositories.CheckinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckInService {
    private final CheckinRepository checkinRepository;

    public void registerCheckIn(Attendee attendee) {
        this.verifyCheckInExists(attendee.getId());
        CheckIn newCheckIn = new CheckIn();
        newCheckIn.setAttendee(attendee);
        newCheckIn.setCreatedAt(LocalDateTime.now());

        this.checkinRepository.save(newCheckIn);
    }

    private void verifyCheckInExists(Integer attendeeId) {
        Optional<CheckIn> isCheckedIn = this.getCheckInByAttendeeId(attendeeId);

        if (isCheckedIn.isPresent()) throw new CheckInAlreadyExistsException("Attendee already checked in");
    }

    public Optional<CheckIn> getCheckInByAttendeeId(Integer attendeeId) {
        return this.checkinRepository.findByAttendeeId(attendeeId);
    }

    public List<CheckIn> getAllCheckIns() {
        return this.checkinRepository.findAll();
    }

    public Optional<CheckIn> getCheckInById(Integer checkInId) {
        return this.checkinRepository.findById(checkInId);
    }

    public void deleteCheckInById(Integer checkInId) {
        Optional<CheckIn> checkIn = getCheckInById(checkInId);

        if (checkIn.isPresent())
            this.checkinRepository.deleteById(checkInId);
    }

    public void deleteCheckInByAttendeeId(Integer attendeeId) {
        Optional<CheckIn> checkIn = getCheckInByAttendeeId(attendeeId);

        checkIn.ifPresent(in -> this.checkinRepository.deleteById(in.getId()));
    }
}
