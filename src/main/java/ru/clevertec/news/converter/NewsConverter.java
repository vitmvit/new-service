package ru.clevertec.news.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.news.dto.NewsDto;
import ru.clevertec.news.dto.create.NewsCreateDto;
import ru.clevertec.news.dto.update.NewsUpdateDto;
import ru.clevertec.news.model.News;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsConverter {

    NewsDto convert(News source);

    News convert(NewsCreateDto source);

    News convert(NewsUpdateDto source);

    News merge(@MappingTarget News news, NewsUpdateDto dto);
}