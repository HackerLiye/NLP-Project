package com.company;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.analysis.Analyzer;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

public class Main {
    @SuppressWarnings("deprecation")
    public static final String INDEX_PATH = "index";

    public static void main(String[] args) throws IOException, ParseException {

//        这个是创建索引的函数，创建过后就不用运行了
//        Indexer.walk("processed");

            System.out.println("请输入查询内容:");
            Scanner sc = new Scanner(System.in);
            String querystr = "hurricane";
            System.out.println("q = "+querystr);
            int hitsPerPage = 5;
            Directory index = FSDirectory.open(Paths.get(INDEX_PATH));
            IndexReader reader = DirectoryReader.open(index);
//            System.out.println("实际文档数：" + reader.numDocs());
            StandardAnalyzer analyzer = new StandardAnalyzer();

            Query query = new QueryParser("text", analyzer).parse(querystr);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
            searcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Processor.getWordFreq(docId, querystr);
                Processor.getDocFreq(querystr);
                Document d = searcher.doc(docId);
                System.out.println(hits[i].score +" "+ d.get("docno"));
                System.out.println(d.get("text"));
            }
            reader.close();
        }
}