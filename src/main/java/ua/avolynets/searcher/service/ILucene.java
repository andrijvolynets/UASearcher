package ua.avolynets.searcher.service;

import ua.avolynets.searcher.entities.IPage;

import java.util.List;
import java.util.Map;

/**
 * Created by Andriy on 10.08.2015.
 */
public interface ILucene {
    List<IPage> search(long actualVersion, Map<String, IPage> globalPages, String keyword);
}
