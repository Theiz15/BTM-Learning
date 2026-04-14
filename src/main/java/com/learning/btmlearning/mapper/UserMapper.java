package com.learning.btmlearning.mapper;

import com.learning.btmlearning.dto.response.UserAdminResponse;
import com.learning.btmlearning.dto.response.UserProfile;
import com.learning.btmlearning.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
//    @Mapping(target = "role", source = "role.name")
    UserProfile toUserProfile(User user);

    @Mapping(source = "role", target = "role")
    UserAdminResponse toAdminResponse(User user);

    default String map(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
