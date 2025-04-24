package com.sarveshsawant.webdev.accountauth.mapper;

public interface Mapper<EntityClass, DtoClass> {
    DtoClass mapTo(EntityClass entityClass);
    EntityClass mapFrom(DtoClass dtoClass);
}
