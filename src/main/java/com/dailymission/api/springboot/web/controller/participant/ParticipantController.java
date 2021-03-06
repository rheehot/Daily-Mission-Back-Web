package com.dailymission.api.springboot.web.controller.participant;

import com.dailymission.api.springboot.security.CurrentUser;
import com.dailymission.api.springboot.security.UserPrincipal;
import com.dailymission.api.springboot.web.dto.participant.ParticipantSaveRequestDto;
import com.dailymission.api.springboot.web.service.participant.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/api/participant")
    @PreAuthorize("hasRole('USER')")
    public Long save(@RequestBody ParticipantSaveRequestDto requestDto, @CurrentUser UserPrincipal userPrincipal){

        return participantService.save(requestDto, userPrincipal);
    }

}
