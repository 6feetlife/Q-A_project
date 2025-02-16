package com.springboot.likes.controller;

import com.springboot.auth.utils.MemberDetails;
import com.springboot.likes.service.LikesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v11/likes")
public class LikesController {
    private final LikesService likesService;


    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    @PostMapping("/{questionId}")
    public ResponseEntity likesPost(@AuthenticationPrincipal MemberDetails memberDetails,
                                    @PathVariable("questionId") long questionId) {

        likesService.createLikes(questionId, memberDetails);

        return new ResponseEntity(HttpStatus.CREATED);
    }
}
