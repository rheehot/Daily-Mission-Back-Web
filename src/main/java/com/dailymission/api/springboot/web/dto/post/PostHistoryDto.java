package com.dailymission.api.springboot.web.dto.post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostHistoryDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Long userId;
    private String userName;

    @Builder
    public PostHistoryDto(LocalDateTime datetime, Long userId, String userName){
        this.userId = userId;
        this.userName = userName;

        // 새벽 3시 이전제출은 이전날 제출로 인식
        LocalDateTime check = LocalDateTime.of(datetime.getYear(),datetime.getMonth(),datetime.getDayOfMonth(), 3,0,0);
        if(datetime.isBefore(check)){
            // 하루전 으로 변경
            date = datetime.minusDays(1).toLocalDate();
        }else{
            date = datetime.toLocalDate();
        }
    }
}
