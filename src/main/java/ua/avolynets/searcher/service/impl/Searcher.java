package ua.avolynets.searcher.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ua.avolynets.searcher.entities.BuilderPage;
import ua.avolynets.searcher.entities.IPage;
import ua.avolynets.searcher.service.ILucene;
import ua.avolynets.searcher.service.ISearcher;


import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Andriy on 09.08.2015.
 */
@Service
public class Searcher implements ISearcher {
    private static final Logger logger = LoggerFactory.getLogger(Searcher.class);

    @Autowired
    private BuilderPage builderPage;


    @Autowired
    @Qualifier("L1")
    private ILucene lucene;

    private static final int MAX_DEEP_LINKS = 3;

    private static AtomicLong version = new AtomicLong(1);

    private static Map<String,IPage> pages = new HashMap();

    private static final Object monitorIncreasePages = new Object();

    private static final int QUEUE_SIZE = 2000000;

    private ThreadPoolExecutor executorsVisitors;
    public Searcher(){
        ArrayBlockingQueue poolsQueues = new ArrayBlockingQueue<>( QUEUE_SIZE, true );
        executorsVisitors = new ThreadPoolExecutor( 10, 50, 10000, TimeUnit.MILLISECONDS, poolsQueues );
        executorsVisitors.prestartCoreThread();
    }

    @Override
    public void addUrlToIndex(String url) {
        long l = System.currentTimeMillis();
        if(null != url && !url.isEmpty()){
            addUrlToIndex(url,0);
        }
        logger.debug(" Time processing:" + (System.currentTimeMillis() - l) + "pages: " + pages.size());
    }

    public void addUrlToIndex(@Nonnull String url,int deepLinks) {
        if(deepLinks >= MAX_DEEP_LINKS) {
            logger.debug("pages size: "+pages.size());
            return;
        }
        if(pages.containsKey(url)) {
           return;
        }

        IPage p;
        synchronized (monitorIncreasePages) {
            if(pages.containsKey(url)) {
                return;
            }
            p = builderPage.createPage(url);
            pages.put(url, p);
        }

        //need process in thread
        processPage(p, deepLinks);
    }

    @Override
    public List<IPage> search(String phrase) {
        return lucene.search(version.get(),pages,phrase);
    }

    @Override
    public String cache(String url) {
        IPage p = pages.get(url);
        if(null != p){
            return p.getText();
        }else{
            return "Cache for page not found";
        }
    }

    private void processPage(IPage page,int deepLinks){

        Callable<IPage> c = new Callable<IPage>() {
            @Override
            public IPage call() throws Exception {
                builderPage.parse(page);
                if(page.getText()==null || page.getText().isEmpty() ){
                    removePage(page);
                    version.incrementAndGet();
                    return null;
                }

                for(String url:page.getChildLinks()){
                    addUrlToIndex(url,deepLinks+1);
                }

                version.incrementAndGet();
                return page;
            }
        };
        Future<IPage> f = this.executorsVisitors.submit(c);
        /// not processing future yet

    }

    private void removePage(IPage page) {
        synchronized (monitorIncreasePages) {
            pages.remove(page.getLink());
        }
    }
}
