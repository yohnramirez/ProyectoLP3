package microservice.auth.mapper;

import microservice.auth.dto.UserDto;
import microservice.auth.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    List<UserDto> toUserDtoList(List<User> users);
}
