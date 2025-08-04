package com.umitakbulut.reference_manager.mapper;

import com.umitakbulut.reference_manager.dto.request.RegisterRequestDTO;
import com.umitakbulut.reference_manager.dto.response.UserResponseDTO;
import com.umitakbulut.reference_manager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(RegisterRequestDTO request);
    
    @Mapping(target = "password", ignore = true)
    UserResponseDTO toDto(User user);

    List<UserResponseDTO> toDtoList(List<User> users);
}