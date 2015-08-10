package ua.avolynets.searcher.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.avolynets.searcher.entities.IPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andriy on 10.08.2015.
 */
@Service("L1")
public class Lucene implements ILucene {
    private static final Logger logger = LoggerFactory.getLogger(Lucene.class);
    private static final int MAX_RECORDS = 10;

    private long lastIndexVersion = 0;
    private Directory lastIndex;
    private  StandardAnalyzer lastAnalyzer;

    @Override
    public List<IPage> search(long actualVersion, Map<String, IPage> globalPages, String keyword){
        Directory index;
        StandardAnalyzer analyzer;
        synchronized (this){
            index = getActualIndex(actualVersion,globalPages);
            analyzer = lastAnalyzer;
        }

        List<IPage> result = new ArrayList();

        try {
            Query q = new QueryParser( "text", analyzer).parse(keyword);

            // 3. search
//            int hitsPerPage = MAX_RECORDS;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
//            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);//,true
//            searcher.search(q, collector);
//            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            TopDocs topDoc = searcher.search(q,MAX_RECORDS, Sort.RELEVANCE);
            ScoreDoc[] hits = topDoc.scoreDocs;

            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                result.add(globalPages.get(d.get("url")));
            }
            reader.close();


        } catch (ParseException ex) {
            logger.error(ex.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }


        return result;
    }

    private Directory getActualIndex(long actualVersion, Map<String,IPage> globalPages){
        if(lastIndexVersion == actualVersion){
            return lastIndex;
        }

        // 0. Specify the analyzer for tokenizing text.
        // The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();
        // 1. create the index
        Directory index = new RAMDirectory();

        Map<String,IPage> pages =  new HashMap(globalPages);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        try(IndexWriter w = new IndexWriter(index, config);) {
            for (IPage page : pages.values()) {
                addDoc(w, page);
            }

        }catch (IOException ex){
            logger.error(ex.getMessage());
            return lastIndex;
        }

        lastIndex = index;
        lastIndexVersion = actualVersion;
        lastAnalyzer = analyzer;
        return index;

    }

    private void addDoc(IndexWriter w, IPage page) throws IOException {
        Document doc = new Document();
        String title =  page.getTitle() == null ? "" : page.getTitle();
        if(null == page.getText()) return;

        StringBuilder text = new StringBuilder(page.getLink());
        text.append(" ").append(title).append(" ").append(page.getText());
        doc.add(new TextField("text",text.toString(), Field.Store.YES));

        // use a string field for link because we don't want it tokenized
        doc.add(new StringField("url", page.getLink(), Field.Store.YES));
//        doc.add(new StringField("title", title, Field.Store.YES));
        w.addDocument(doc);
    }
}
