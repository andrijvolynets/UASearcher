package ua.avolynets.searcher.view;

/**
 * Created by Andriy on 10.08.2015.
 */
public class ViewDivCenter  extends ViewHtml {

    public ViewDivCenter(String html){
        sb = new StringBuilder();
        sb.append("<div align=\"center\">");
        sb.append(html);
        sb.append("</div>");
    }
}
