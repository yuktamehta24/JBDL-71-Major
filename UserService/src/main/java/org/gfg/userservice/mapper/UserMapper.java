package org.gfg.userservice.mapper;

import lombok.experimental.UtilityClass;
import org.gfg.userservice.dto.CreateUserRequest;
import org.gfg.userservice.enums.UserStatus;
import org.gfg.userservice.model.User;

@UtilityClass
public class UserMapper {

    public User mapToUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNo(request.getPhoneNo())
                .userIdentificationType(request.getUserIdentificationType())
                .userIdentificationValue(request.getUserIdentificationValue())
                .userStatus(UserStatus.ACTIVE).build();

    }
}
