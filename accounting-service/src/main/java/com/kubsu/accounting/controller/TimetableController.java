package com.kubsu.accounting.controller;

import com.kubsu.accounting.dto.NumTimeClassHeldResponseDTO;
import com.kubsu.accounting.model.Timetable;
import com.kubsu.accounting.service.TimetableService;
import com.kubsu.accounting.service.UserDetailsImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timetables")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @GetMapping("/number-time-classes-held")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('STUDENT') or hasRole('ADMIN')")
    public List<NumTimeClassHeldResponseDTO> getAllNumTimeClassHeld() {
        return timetableService.getAllNumTimeClassHeld()
                .stream()
                .map(NumTimeClassHeldResponseDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("")
    @PreAuthorize("hasRole('LECTURER') or hasRole('MODERATOR') or hasRole('STUDENT') or hasRole('ADMIN')")
    public List<Timetable> getAllTimetable(@RequestParam("start_date") OffsetDateTime startDate,
                                           @RequestParam("end_date") OffsetDateTime endDate) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return timetableService.getAllTimetable(startDate, endDate, userDetails.getId());
    }
}
