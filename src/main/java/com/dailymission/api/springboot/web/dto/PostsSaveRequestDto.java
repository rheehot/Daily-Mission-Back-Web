package com.dailymission.api.springboot.web.dto;

import com.dailymission.api.springboot.web.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {
    private String title;
    private String content;
    private String author;

    @Builder
    public PostsSaveRequestDto(String title, String content, String author){
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Posts toEntitiy(){
        return Posts.builder()
                .title(content)
                .content(content)
                .author(author)
                .build();
    }
}
