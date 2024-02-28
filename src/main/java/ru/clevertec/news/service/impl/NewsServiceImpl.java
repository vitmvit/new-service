package ru.clevertec.news.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.clevertec.news.converter.NewsConverter;
import ru.clevertec.news.dto.NewsDto;
import ru.clevertec.news.dto.create.NewsCreateDto;
import ru.clevertec.news.dto.update.NewsUpdateDto;
import ru.clevertec.news.exception.EmptyListException;
import ru.clevertec.news.exception.EntityNotFoundException;
import ru.clevertec.news.feign.CommentClient;
import ru.clevertec.news.model.News;
import ru.clevertec.news.repository.NewsRepository;
import ru.clevertec.news.service.NewsService;

/**
 * Реализация сервисного слоя для работы с новостями.
 */
@Service
@Transactional
@AllArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsConverter newsConverter;
    private final CommentClient commentClient;

    /**
     * Возвращает информацию о новости по заданному id.
     *
     * @param id новости
     * @return информация о новости
     * @throws EntityNotFoundException если новость не найдена
     */
    @Override
    public NewsDto findNewsById(Long id) {
        var news = newsConverter.convert(newsRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        news.setComments(null);
        return news;
    }

    /**
     * Возвращает информацию о новости по заданному id вместе с комментариями.
     *
     * @param offset смещение страницы комментариев
     * @param limit  лимит элементов на странице комментариев
     * @param id     id новости
     * @return информация о новости с комментариями
     * @throws EntityNotFoundException если новость не найдена
     */
    @Override
    public NewsDto findNewsByIdWithComments(Integer offset, Integer limit, Long id) {
        var news = newsConverter.convert(newsRepository.findById(id).orElseThrow(EntityNotFoundException::new));
        news.setComments(commentClient.getByNewsId(offset, limit, id).stream().toList());
        return news;
    }

    /**
     * Возвращает страницу с информацией о новостях.
     *
     * @param offset смещение страницы
     * @param limit  лимит элементов на странице
     * @return страница с информацией о новостях
     * @throws EmptyListException если список новостей пуст
     */
    @Override
    public Page<NewsDto> findAllNews(Integer offset, Integer limit) {
        Page<News> newsPage = newsRepository.findAll(PageRequest.of(offset, limit));
        newsPage.stream().findAny().orElseThrow(EmptyListException::new);
        return newsPage.map(newsConverter::convert);
    }

    /**
     * Возвращает список новостей, найденных по фрагменту контента.
     *
     * @param offset   смещение страницы
     * @param limit    лимит элементов на странице
     * @param fragment фрагмент контента
     * @return список новостей
     * @throws EmptyListException если список новостей пуст
     */
    @Override
    public Page<NewsDto> searchNewsByText(Integer offset, Integer limit, String fragment) {
        var newsPage = newsRepository.findByTextContaining(PageRequest.of(offset, limit), fragment);
        newsPage.stream().findAny().orElseThrow(EmptyListException::new);
        return newsPage.map(newsConverter::convert);
    }

    /**
     * Возвращает список новостей, найденных по фрагменту заголовка.
     *
     * @param offset   смещение страницы
     * @param limit    лимит элементов на странице
     * @param fragment фрагмент заголовка
     * @return список новостей
     * @throws EmptyListException если список новостей пуст
     */
    @Override
    public Page<NewsDto> searchNewsByTitle(Integer offset, Integer limit, String fragment) {
        var newsPage = newsRepository.findByTitleContaining(PageRequest.of(offset, limit), fragment);
        newsPage.stream().findAny().orElseThrow(EmptyListException::new);
        return newsPage.map(newsConverter::convert);
    }

    /**
     * Создает новую новость на основе данных из DTO.
     *
     * @param dto данные для создания новости
     * @return созданная новость
     */
    @Override
    public NewsDto createNews(NewsCreateDto dto) {
        var news = newsConverter.convert(dto);
        return newsConverter.convert(newsRepository.save(news));
    }

    /**
     * Обновляет информацию о новости на основе данных из DTO.
     *
     * @param newsUpdateDto данные для обновления новости
     * @return обновленная новость
     * @throws EntityNotFoundException если новость не найдена
     */
    @Override
    public NewsDto updateNews(NewsUpdateDto newsUpdateDto) {
        var news = newsRepository.findById(newsUpdateDto.getId()).orElseThrow(EntityNotFoundException::new);
        newsConverter.merge(news, newsUpdateDto);
        return newsConverter.convert(newsRepository.save(news));
    }

    /**
     * Удаляет новость по заданному id.
     *
     * @param id новости
     */
    @Override
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
}
