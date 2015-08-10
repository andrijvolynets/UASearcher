package ua.avolynets.searcher.entities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andriy on 10.08.2015.
 */
@Service
public class BuilderPage {
    private static final Logger logger = LoggerFactory.getLogger(BuilderPage.class);

    public IPage createPage(String url){
        Page page = new Page();
        page.setLink(url);
        return page;
    }

    public void parse(IPage page){

        List<String> childLinks = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(page.getLink()).get();
        } catch (IOException e) {
            logger.warn("Connection problem: "+e.getMessage());
            return;
        }
        Elements links = doc.getElementsByTag("a");
        for (Element link : links) {
            String linkHref = link.attr("href");
            if(!linkHref.startsWith("http")){
                linkHref = "http:" + linkHref;
            }
            childLinks.add(linkHref);
            logger.debug("child links: " + linkHref);
        }

        page.setTitle(doc.getElementsByTag("title").get(0).text());
        page.setText(doc.text());
        page.setChildLinks(childLinks);
    }
}
