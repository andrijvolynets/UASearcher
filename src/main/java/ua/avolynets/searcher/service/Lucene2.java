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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;
import ua.avolynets.searcher.entities.IPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Andriy on 10.08.2015.
 */

@Service("L2")
public class Lucene2 implements ILucene {
    @Override
    public List<IPage> search(long actualVersion, Map<String, IPage> globalPages, String keyword) {
        List<IPage> pages = new ArrayList();
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try {
            IndexWriter w = new IndexWriter(index, config);
            for (IPage p : globalPages.values()) {
                addDoc(w, p);
            }
            w.close();

            // 2. query
            String querystr = keyword;

            // the "title" arg specifies the default field to use
            // when no field is explicitly specified in the query.
            Query q = new QueryParser("title", analyzer).parse(querystr);

            // 3. search
            int hitsPerPage = 10;
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);//,true
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            // 4. display results
            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
              //  System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));

                pages.add(globalPages.get(d.get("isbn")));
            }

            // reader can only be closed when there
            // is no need to access the documents any more.
            reader.close();

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return pages;
    }

    private  void addDoc(IndexWriter w, IPage page) throws IOException {
        Document doc = new Document();

        String title =  page.getTitle() == null ? "" : page.getTitle();
        if(null == page.getText()) return;

        doc.add(new TextField("title", title+" "+ page.getText(), Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("isbn", page.getLink(), Field.Store.YES));
        w.addDocument(doc);
    }
}
