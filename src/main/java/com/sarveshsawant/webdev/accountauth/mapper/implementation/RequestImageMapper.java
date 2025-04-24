package com.sarveshsawant.webdev.accountauth.mapper.implementation;
import com.sarveshsawant.webdev.accountauth.domain.dto.RequestImageDto;
import com.sarveshsawant.webdev.accountauth.domain.entities.ImageEntity;
import com.sarveshsawant.webdev.accountauth.mapper.Mapper;
import org.modelmapper.ModelMapper;

public class RequestImageMapper implements Mapper<ImageEntity, RequestImageDto> {

    private ModelMapper modelMapper;

    public RequestImageMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public RequestImageDto mapTo(ImageEntity imageEntity) {
        return modelMapper.map(imageEntity, RequestImageDto.class);
    }

    @Override
    public ImageEntity mapFrom(RequestImageDto requestImageDto) {
        return modelMapper.map(requestImageDto, ImageEntity.class);
    }
}