package com.sarveshsawant.webdev.accountauth.mapper.implementation;

import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseImageDto;
import com.sarveshsawant.webdev.accountauth.domain.dto.ResponseUserDto;
import com.sarveshsawant.webdev.accountauth.domain.entities.ImageEntity;
import com.sarveshsawant.webdev.accountauth.domain.entities.UserEntity;
import com.sarveshsawant.webdev.accountauth.mapper.Mapper;
import org.modelmapper.ModelMapper;

import java.awt.*;

public class ResponseImageMapper implements Mapper<ImageEntity, ResponseImageDto> {

    private ModelMapper modelMapper;

    ResponseImageMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseImageDto mapTo(ImageEntity imageEntity) {
        return modelMapper.map(imageEntity, ResponseImageDto.class);
    }

    @Override
    public ImageEntity mapFrom(ResponseImageDto responseImageDto) {
        return modelMapper.map(responseImageDto, ImageEntity.class);
    }
}
