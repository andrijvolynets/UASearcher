package ua.avolynets.searcher.view;

import ua.avolynets.searcher.entities.IPage;

import java.util.List;

/**
 * Created by Andriy on 10.08.2015.
 */
public class ViewResultSearch extends ViewHtml{

    public ViewResultSearch(List<IPage> pages, String phrase){

        sb = new StringBuilder("<div align='left'>");
        sb.append("Searching <b>"+phrase+"</b> <br/><br/>");
        int i=1;
        for(IPage page:pages){
            sb.append("<a href='").append(page.getLink()).append("' style='color:blue;font-weight:bold;' >");
            sb.append(i+". ");
            sb.append(page.getTitle());
            sb.append("</a>");
            sb.append("&nbsp;&nbsp;&nbsp;<a href='cache?url=").append(page.getLink()).append("' style='color:grey;'>");
            sb.append("cache");
            sb.append("</a><br/>");
            sb.append("<a href='").append(page.getLink()).append("' >");
            sb.append(page.getLink());
            sb.append("</a><br/><br/>");
            i++;
        }
        sb.append("</div>");
    }


}
