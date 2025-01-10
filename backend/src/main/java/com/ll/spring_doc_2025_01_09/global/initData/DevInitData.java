package com.ll.spring_doc_2025_01_09.global.initData;

import com.ll.spring_doc_2025_01_09.domain.member.member.service.MemberService;
import com.ll.spring_doc_2025_01_09.domain.post.post.service.PostService;
import com.ll.spring_doc_2025_01_09.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class DevInitData {
    private final MemberService memberService;
    private final PostService postService;
    @Autowired
    @Lazy
    private DevInitData self;

    @Bean
    public ApplicationRunner devInitDataApplicationRunner() {
        return args -> {
            Ut.file.downloadByHttp("http://localhost:8080/v3/api-docs/apiV1", ".");
        };
    }
}
