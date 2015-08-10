package ua.avolynets.searcher.entities;

import java.util.List;

/**
 * Created by Andriy on 09.08.2015.
 */
public interface IPage {

    public List<String> getChildLinks();

    public String getLink();

    public String getText();

    public void setText(String text);

    public void setChildLinks(List<String> childLinks);

    public String getTitle();

    public void setTitle(String title);
}
