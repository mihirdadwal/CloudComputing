package com.sarveshsawant.webdev.accountauth.mapper.implementation;

import com.sarveshsawant.webdev.accountauth.domain.dto.RequestUserPostDto;
import com.sarveshsawant.webdev.accountauth.domain.entities.UserEntity;
import com.sarveshsawant.webdev.accountauth.mapper.Mapper;
import org.modelmapper.ModelMapper;

public class RequestUserMapper implements Mapper<UserEntity, RequestUserPostDto> {

    private ModelMapper modelMapper;

    public RequestUserMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public RequestUserPostDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, RequestUserPostDto.class);
    }

    @Override
    public UserEntity mapFrom(RequestUserPostDto requestUserPostDto) {
        return modelMapper.map(requestUserPostDto, UserEntity.class);
    }
}
