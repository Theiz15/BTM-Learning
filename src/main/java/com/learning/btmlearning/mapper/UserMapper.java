package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.response.UserProfile;
import com.learning.btmlearning.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
//    @Mapping(target = "role", source = "role.name")
    UserProfile toUserProfile(User user);
}
