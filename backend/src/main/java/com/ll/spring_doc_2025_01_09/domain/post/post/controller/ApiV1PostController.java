package com.ll.spring_doc_2025_01_09.domain.post.post.controller;

import com.ll.spring_doc_2025_01_09.domain.member.member.entity.Member;
import com.ll.spring_doc_2025_01_09.domain.post.post.dto.PostDto;
import com.ll.spring_doc_2025_01_09.domain.post.post.dto.PostWithContentDto;
import com.ll.spring_doc_2025_01_09.domain.post.post.entity.Post;
import com.ll.spring_doc_2025_01_09.domain.post.post.service.PostService;
import com.ll.spring_doc_2025_01_09.global.exceptions.ServiceException;
import com.ll.spring_doc_2025_01_09.global.rq.Rq;
import com.ll.spring_doc_2025_01_09.global.rsData.RsData;
import com.ll.spring_doc_2025_01_09.standard.page.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "ApiV1PostController", description = "API 글 컨트롤러")
public class ApiV1PostController {
    private final PostService postService;
    private final Rq rq;

    record PostStatisticsResBody(
            long totalPostCount,
            long totalPublishedPostCount,
            long ListedPostCount
    ) {}

    @GetMapping("/statistics")
    @Transactional(readOnly = true)
    @Operation(summary = "통계정보")
    public PostStatisticsResBody statistic() {
        Member actor = rq.getActor();

        return new PostStatisticsResBody(
                10,
                10,
                10
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "공개글 다건 조회")
    public PageDto<PostDto> items(
            @RequestParam(defaultValue = "title") String searchKeywordType,
            @RequestParam(defaultValue = "") String searchKeyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new PageDto<>(
                postService.findByListedPaged(true, searchKeywordType, searchKeyword, page, pageSize)
                        .map(PostDto::new)
        );
    }

    @GetMapping("/mine")
    @Transactional(readOnly = true)
    @Operation(summary = "내글 다건 조회")
    public PageDto<PostDto> mine(
            @RequestParam(defaultValue = "title") String searchKeywordType,
            @RequestParam(defaultValue = "") String searchKeyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Member author = rq.getActor();

        return new PageDto<>(
                postService.findByAuthorPaged(author, searchKeywordType, searchKeyword, page, pageSize)
                        .map(PostDto::new)
        );
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회", description = "비밀글은 작성자만 조회 가능")
    public PostWithContentDto item(@PathVariable long id) {
        Post post = postService.findById(id).get();

        if(!post.isPublished()) {
            Member author = rq.getActor();

            if(author == null) {
                throw new ServiceException("401-1", "로그인이 필요합니다.");
            }

            post.checkActorCanRead(author);
        }

        return new PostWithContentDto(postService.findById(id).get());
    }

    record PostWriteReqBody(
            @NotBlank
            String title,

            @NotBlank
            String content,

            boolean published,
            boolean listed
    ) {}

    @PostMapping
    @Transactional
    @Operation(summary = "글 작성")
    public RsData<PostWithContentDto> write(
            @RequestBody @Valid PostWriteReqBody reqBody
    ) {
        Member author = rq.findByActor().get();
        Post post = postService.write(author, reqBody.title, reqBody.content, reqBody.published, reqBody.listed);

        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostWithContentDto(post)
        );
    }

    record PostModifyReqBody(
            @NotBlank
            @Length(min = 2, max = 100)
            String title,
            @NotBlank
            @Length(min = 2, max = 10000000)
            String content,

            boolean published,
            boolean listed
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "글 수정")
    public RsData<PostWithContentDto> modify(
            @PathVariable long id,
            @RequestBody @Valid PostModifyReqBody reqBody
    ) {
        Member author = rq.getActor();
        Post post = postService.findById(id).get();

        post.checkActorCanModify(author);
        postService.modify(post, reqBody.title, reqBody.content, reqBody.published, reqBody.listed);

        postService.flush();

        return new RsData<>(
                "200-1",
                "%d번 글이 수정되었습니다.".formatted(post.getId()),
                new PostWithContentDto(post)
        );
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "글 삭제", description = "작성자 본인 뿐 아니라 관리자도 삭제 가능")
    public RsData<Void> delete(@PathVariable long id) {
        Member author = rq.getActor();
        Post post = postService.findById(id).get();

        post.checkActorCanDelete(author);
        postService.delete(post);

        return new RsData<>(
                "200-1",
                "%d번 글이 삭제되었습니다.".formatted(post.getId())
        );
    }
}