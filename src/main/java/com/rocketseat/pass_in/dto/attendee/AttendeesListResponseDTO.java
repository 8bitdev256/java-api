package com.rocketseat.pass_in.dto.attendee;

import java.util.List;

public record AttendeesListResponseDTO(List<AttendeeDetails> attendees, int total) {
}
