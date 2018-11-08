package com.company;

import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

public class Processor {
    public static void getWordFreq(int docId, String word) throws IOException {
        Directory index = FSDirectory.open(Paths.get(Main.INDEX_PATH));
        IndexReader reader = DirectoryReader.open(index);
        Terms termVector = reader.getTermVector(docId,"text");
        TermsEnum itr = termVector.iterator();
        BytesRef term = null;
        while ((term = itr.next()) != null) {
            String termText = term.utf8ToString();
            if(termText.equals(word)) {
                long termFreq = itr.totalTermFreq();
                System.out.println("term: " + termText + ", termFreq = " + termFreq);
            }
        }
    }
    public static void getDocFreq(String query) throws IOException{
        Directory index = FSDirectory.open(Paths.get(Main.INDEX_PATH));
        IndexReader reader = DirectoryReader.open(index);
        Term myTerm = new Term("text", query);
        System.out.println("total: "+reader.docFreq(myTerm));
    }
}
