package com.example.library.service;

import com.example.library.dto.request.MemberRequest;
import com.example.library.dto.response.MemberResponse;
import com.example.library.entity.Member;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.mapper.MemberMapper;
import com.example.library.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public MemberService(MemberRepository memberRepository,
                         MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable).map(memberMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        return memberMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> searchByName(String name) {
        return memberRepository.searchByName(name)
                .stream().map(memberMapper::toResponse).toList();
    }

    public MemberResponse create(MemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail()))
            throw new DuplicateResourceException("Member with email '" + request.getEmail() + "' already exists");
        return memberMapper.toResponse(memberRepository.save(memberMapper.toEntity(request)));
    }

    public MemberResponse update(Long id, MemberRequest request) {
        Member member = getOrThrow(id);
        if (!member.getEmail().equals(request.getEmail()) && memberRepository.existsByEmail(request.getEmail()))
            throw new DuplicateResourceException("Member with email '" + request.getEmail() + "' already exists");
        memberMapper.updateFromRequest(request, member);
        return memberMapper.toResponse(memberRepository.save(member));
    }

    public void delete(Long id) {
        getOrThrow(id);
        memberRepository.deleteById(id);
    }

    private Member getOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }
}