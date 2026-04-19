package com.example.library.mapper;

import com.example.library.dto.request.MemberRequest;
import com.example.library.dto.response.MemberResponse;
import com.example.library.entity.Member;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(target = "membershipDate", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    Member toEntity(MemberRequest request);

    MemberResponse toResponse(Member member);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "membershipDate", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    void updateFromRequest(MemberRequest request, @MappingTarget Member member);
}