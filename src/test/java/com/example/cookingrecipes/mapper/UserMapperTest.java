package com.example.cookingrecipes.mapper;

import com.example.cookingrecipes.dto.UserDto;
import com.example.cookingrecipes.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @InjectMocks
    private UserMapper userMapper;

    @Test
    void toDto_ShouldMapAllFields() {
        User user = new User();

        UserDto dto = userMapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        UserDto dto = new UserDto();

        User user = userMapper.toEntity(dto);

        assertThat(user.getId()).isEqualTo(dto.getId());
        assertThat(user.getUsername()).isEqualTo(dto.getUsername());
    }
}