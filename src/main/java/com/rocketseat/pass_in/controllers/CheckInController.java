package com.rocketseat.pass_in.controllers;

import com.rocketseat.pass_in.domain.checkin.CheckIn;
import com.rocketseat.pass_in.services.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/check-ins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @GetMapping
    public List<CheckIn> getAllCheckIns() {
        return this.checkInService.getAllCheckIns();
    }

    @DeleteMapping("/{checkInId}")
    public void deleteCheckInById(@PathVariable Integer checkInId) {
        this.checkInService.deleteCheckInById(checkInId);
    }
}
