package ua.avolynets.searcher.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andriy on 10.08.2015.
 */
class Page implements IPage {

    private List<String> childLinks = new ArrayList();
    private String link;
    private String text;
    private String title;

    public List<String> getChildLinks() {
        return childLinks;
    }

    public void setChildLinks(List<String> childLinks) {
        this.childLinks = childLinks;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        return link.equals(page.link);

    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }
}
