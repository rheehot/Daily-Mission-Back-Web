package com.dailymission.api.springboot.web.service.participant;

import com.dailymission.api.springboot.exception.ResourceNotFoundException;
import com.dailymission.api.springboot.security.UserPrincipal;
import com.dailymission.api.springboot.web.dto.participant.ParticipantListResponseDto;
import com.dailymission.api.springboot.web.dto.participant.ParticipantResponseDto;
import com.dailymission.api.springboot.web.dto.participant.ParticipantSaveRequestDto;
import com.dailymission.api.springboot.web.dto.participant.ParticipantUpdateRequestDto;
import com.dailymission.api.springboot.web.repository.mission.Mission;
import com.dailymission.api.springboot.web.repository.mission.MissionRepository;
import com.dailymission.api.springboot.web.repository.participant.Participant;
import com.dailymission.api.springboot.web.repository.participant.ParticipantRepository;
import com.dailymission.api.springboot.web.repository.user.User;
import com.dailymission.api.springboot.web.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    private final MissionRepository missionRepository;

    private final UserRepository userRepository;

    @Transactional
    public Long save(ParticipantSaveRequestDto requestDto, UserPrincipal userPrincipal){
        // user
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        // mission
        Mission mission = missionRepository.findById(requestDto.getMission().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mission", "id", requestDto.getMission().getId()));

        // 이미 참여중인지 확인
        Optional<Participant> optional = participantRepository.findByMissionAndUser(mission,user);
        if(optional.isPresent()){
            // 강퇴된 회원
            if(optional.get().isBanned()){
                throw new IllegalArgumentException("강퇴된 미션에는 참여할 수 없습니다.");
            }else{
                throw new IllegalArgumentException("이미 참여중인 미션입니다.");
            }
        }

        // 종료 및 삭제여부 확인
        if(!mission.checkStatus()){
            throw new IllegalArgumentException("참여가능한 미션이 아닙니다.");
        }

        // 참여가능 날짜 확인 (시작 날짜가 지났으면 참여 불가능)
        if(!mission.checkStartDate(LocalDate.now())){
            throw new IllegalArgumentException("미션 참여 가능기간이 아닙니다.");
        }

        // 비밀번호 확인
        if(!mission.checkCredential(requestDto.getCredential())){
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        // entity
        Participant participant =  requestDto.toEntity(user);
        return participantRepository.save(participant).getId();
    }

    @Transactional(readOnly = true)
    public ParticipantResponseDto findByMissionAndAccount (Mission mission, User user){
        Optional<Participant> optional = Optional.ofNullable(participantRepository.findByMissionAndUser(mission, user))
                .orElseThrow(()-> new NoSuchElementException("해당 참여내용은 존재하지 않습니다"));

        Participant participant = optional.get();
        return new ParticipantResponseDto(participant);
    }

    @Transactional(readOnly = true)
    public List<ParticipantListResponseDto> findAllByMission (Mission mission){

        return participantRepository.findAllByMission(mission)
                .stream()
                .map(ParticipantListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipantListResponseDto> findAllByAccount (User user){

        return participantRepository.findAllByUser(user)
                .stream()
                .map(ParticipantListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long ban(ParticipantUpdateRequestDto requestDto){
        Optional<Participant> optional = Optional.ofNullable(participantRepository.findByMissionAndUser(requestDto.getMission(), requestDto.getUser()))
                                        .orElseThrow(()-> new NoSuchElementException("미션에 참가하고 있는 사용자가 아닙니다"));

        Participant participant = optional.get();
        participant.ban();

        return participant.getId();
    }
}
