package ua.avolynets.searcher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.avolynets.searcher.service.ISearcher;
import ua.avolynets.searcher.view.ViewDivCenter;
import ua.avolynets.searcher.view.ViewHtml;
import ua.avolynets.searcher.view.ViewResultSearch;



@Controller
@SuppressWarnings("UnusedDeclaration")
public class MainController {

    @Autowired
    ISearcher searcher;

    @RequestMapping(value="/index",method = RequestMethod.GET)
    public String index1(){
        return "/html/index.html";
    }

    @RequestMapping(value="/index",method = RequestMethod.POST)
    @ResponseBody
    public String index2(@RequestParam("q") String uri){
        searcher.addUrlToIndex(uri);
        ViewDivCenter v = new ViewDivCenter( "Start indexing:"+ uri+"<br/><a href='index'>return to index</a>");
        return v.getHtml();
    }

    @RequestMapping(value={"/"},method = RequestMethod.GET)
    public String search3(){
        return "/html/search.html";
    }

    @RequestMapping(value="/search",method = {RequestMethod.POST,RequestMethod.GET},produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String search4(@RequestParam(value = "q",required = false) String phrase){
       if(null == phrase || phrase.isEmpty()){
           return  "<script>window.location='/';</script>";
       }

       ViewHtml v1= new ViewResultSearch(searcher.search(phrase),phrase);

       return v1.getHtml();
    }


    @RequestMapping(value="/cache",method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String cache(@RequestParam("url") String uri){

        return "Cache for:"+ uri+"<br/><br/>"+searcher.cache(uri);
    }


}
