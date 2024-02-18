package ru.clevertec.news.util;

import lombok.Builder;
import ru.clevertec.news.dto.CommentDto;
import ru.clevertec.news.dto.NewsDto;
import ru.clevertec.news.dto.create.NewsCreateDto;
import ru.clevertec.news.dto.update.NewsUpdateDto;
import ru.clevertec.news.model.News;

import java.time.LocalDateTime;
import java.util.List;

@Builder(setterPrefix = "with")
public class NewsTestBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private LocalDateTime time = LocalDateTime.of(2024, 1, 3, 9, 12, 15, 156);

    @Builder.Default
    private String title = "titleOne";

    @Builder.Default
    private String text = "Text";

    @Builder.Default
    private List<CommentDto> commentDtoList = List.of();

    public News buildNews() {
        return new News(id, time, title, text);
    }

    public NewsDto buildNewsDto() {
        return new NewsDto(id, time, title, text, commentDtoList);
    }

    public NewsCreateDto buildNewsCreateDto() {
        return new NewsCreateDto(title, text);
    }

    public NewsUpdateDto buildNewsUpdateDto() {
        var news = new NewsUpdateDto(id);
        news.setText(text);
        news.setTitle(title);
        return news;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}