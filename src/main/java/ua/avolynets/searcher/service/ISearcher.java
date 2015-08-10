package ua.avolynets.searcher.service;

import ua.avolynets.searcher.entities.IPage;

import java.util.List;

/**
 * Created by Andriy on 09.08.2015.
 */
public interface ISearcher {

    void addUrlToIndex(String url);

    List<IPage> search(String phrase);

    String cache(String url);
}
