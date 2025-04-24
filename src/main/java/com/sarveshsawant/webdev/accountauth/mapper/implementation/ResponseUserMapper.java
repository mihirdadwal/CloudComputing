package com.sarveshsawant.webdev.accountauth.mapper.implementation;

import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseUserDto;
import com.sarveshsawant.webdev.accountauth.domain.entities.UserEntity;
import com.sarveshsawant.webdev.accountauth.mapper.Mapper;
import org.modelmapper.ModelMapper;

public class ResponseUserMapper implements Mapper<UserEntity, ResponseUserDto> {

    private ModelMapper modelMapper;

    ResponseUserMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseUserDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, ResponseUserDto.class);
    }

    @Override
    public UserEntity mapFrom(ResponseUserDto responseUserDto) {
        return modelMapper.map(responseUserDto, UserEntity.class);
    }
}
