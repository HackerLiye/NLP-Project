package com.company;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.valuesource.IntFieldSource;
import org.apache.lucene.search.Query;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;


public class MyScoreQuery {

    private static IndexReader indexReader = null;

    static {
        try {
            indexReader = DirectoryReader.open(FileIndexUtil.getDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IndexSearcher getSearcher() {
        if (indexReader == null) {
            try {
                indexReader = DirectoryReader.open(FileIndexUtil.getDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                IndexReader tr = DirectoryReader.openIfChanged((DirectoryReader)indexReader);
                if (tr != null) {
                    indexReader.close();
                    indexReader = tr;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new IndexSearcher(indexReader);
    }

    public void searchByScoreQuery(String queryStr, int hitsPerPage) {

        IndexSearcher searcher = getSearcher();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IntFieldSource source = new IntFieldSource("score_sort");
        FunctionQuery fq = new FunctionQuery(source);
        TopDocs topDocs;

        try {
            Query q = new QueryParser("content", analyzer).parse(queryStr);
            MyCustomScoreQuery query = new MyCustomScoreQuery(q, fq);
            topDocs = searcher.search(query, hitsPerPage);
            int counter = 0;
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                System.out.printf("\n%2d [%s] %s\n", ++counter, scoreDoc.score, document.get("docNo"));
                String content = document.get("content");
                if (content.length() > 300){
                    content = content.substring(0, 297) + "...";
                }
                System.out.println(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class MyCustomScoreQuery extends CustomScoreQuery {

        public MyCustomScoreQuery(Query subQuery, FunctionQuery scoringQuery) {
            super(subQuery, scoringQuery);
        }

        @Override
        protected CustomScoreProvider getCustomScoreProvider(LeafReaderContext context) {
            //默认情况实现的评分是通过原有的评分*传入进来的评分
            return new MyCustomScoreProvider(context);
        }
    }
}
