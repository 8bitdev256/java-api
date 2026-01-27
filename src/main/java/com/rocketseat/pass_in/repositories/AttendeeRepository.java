package com.rocketseat.pass_in.repositories;

import com.rocketseat.pass_in.domain.attendee.Attendee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendeeRepository extends JpaRepository<Attendee, Integer> {
    List<Attendee> findByEventId(String eventId);

    List<Attendee> findByEventId(String eventId, Pageable pageable);

    Optional<Attendee> findByEventIdAndEmail(String eventId, String email);
}
