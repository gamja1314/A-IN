package com.team.ain.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.ain.config.jwt.JwtTokenProvider;
import com.team.ain.dto.MemberResponse;
import com.team.ain.dto.auth.Member;
import com.team.ain.dto.auth.MemberJoin;
import com.team.ain.dto.auth.MemberUpdateDTO;
import com.team.ain.service.FollowerService;
import com.team.ain.service.MemberService;
import com.team.ain.service.PetService;
import com.team.ain.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PetService petService;
    private final FollowerService followerService;
    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody MemberJoin memberJoin) {
        memberService.signup(memberJoin);

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원가입 성공");

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/my")
    public Map<String, Object> getMemberInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Member member = memberService.findByEmail(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("member", member);
        response.put("pet", petService.findByEmail(email));
        response.put("follows", followerService.checkFollwers(member.getId()));
        return response;
    }

    @GetMapping("/{memberId}")
    public Map<String, Object> getSomeoneInfo(@PathVariable Long memberId, HttpServletRequest request) {
        Long userId = jwtTokenProvider.getMemberIdFromRequest(request);

        Map<String, Object> response = new HashMap<>();
        response.put("isFollowing", followerService.isFollowing(userId, memberId));
        response.put("member", memberService.findNameAndProfileUrlById(memberId));
        response.put("pet", petService.getPetByMeberId(memberId));
        response.put("follows", followerService.checkFollwers(memberId));
        // post 추가
        return response;
    }

    //1215 프로필 업뎃
    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody MemberUpdateDTO updateDTO
    ) {
        String email = userDetails.getUsername();
        memberService.updateMemberProfile(email, updateDTO);
        return ResponseEntity.ok("프로필이 성공적으로 업데이트되었습니다.");
    }

    @GetMapping("/{memberId}/followers")
    public List<MemberResponse> getFollowers(@PathVariable Long memberId) {
        return followerService.getFollowers(memberId);
    }

    @GetMapping("/{memberId}/following")
    public List<MemberResponse> getFollowing(@PathVariable Long memberId) {
        return followerService.getFollowing(memberId);
    }
        
}
