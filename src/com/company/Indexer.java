package com.company;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.TrueFileFilter;

public class Indexer {

    public static void walk(String path) throws FileNotFoundException, IOException {
        Directory directory = FSDirectory.open(FileSystems.getDefault().getPath(Main.INDEX_PATH));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(directory, indexWriterConfig);
        w.deleteAll();
        String docno = "";
        String doctype = "";
        String texttype = "";
        String text = "";
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) return;
        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath() );
                //System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
                //System.out.println( "File:" + f.getAbsoluteFile() );
                Scanner sc = new Scanner(f);
                docno = sc.nextLine();
                doctype = sc.nextLine();
                texttype = sc.nextLine();
                if (sc.hasNextLine()) {
                    text = sc.nextLine();
                }
                try {
                    addDoc(w, docno, doctype, texttype, text);
                    System.out.println(docno);
                }
                catch (IllegalArgumentException e){
                    System.out.println(e);
                }
            }
        }
        w.close();
    }

    public static void addDoc(IndexWriter w, String docno, String doctype, String texttype, String text) throws IOException {
        Document doc = new Document();
        FieldType fieldType = new FieldType();
        fieldType.setStored(true);

        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);

        fieldType.setStoreTermVectors(true);

        doc.add(new Field("docno", docno, fieldType));
        doc.add(new Field("doctype", doctype, fieldType));
        doc.add(new Field("texttype", texttype, fieldType));
        doc.add(new Field("text", text, fieldType));
        w.addDocument(doc);
    }
}
