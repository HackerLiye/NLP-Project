package com.company;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Random;
import java.util.Scanner;


public class FileIndexUtil{

    public final static String INDEX_STORE_PATH = "processed";
    private static Directory directory = null;

    static {
        try {
            directory = FSDirectory.open(FileSystems.getDefault().getPath(Main.INDEX_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Directory getDirectory(){
        return directory;
    }

    // 创建索引
    public static void index(boolean hasNew){

        IndexWriter writer = null;
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try{

            writer = new IndexWriter(directory, indexWriterConfig);

            if (hasNew) {
                writer.deleteAll();  //如果我们要新建索引，那么将之前创建的删除
            }

            File file = new File(INDEX_STORE_PATH);
            Document document;
            Random random = new Random();

            for (File f : file.listFiles()) {
                document = new Document();
                Scanner scanner = new Scanner(f);
                String docNo = scanner.nextLine();
                String docType = scanner.nextLine();
                String textType = scanner.nextLine();
                String content = "";
                if (scanner.hasNextLine()) {
                    content = scanner.nextLine();
                }

                document.add(new TextField("docNo", docNo, Field.Store.YES));
                document.add(new TextField("docType", docType, Field.Store.YES));
                document.add(new TextField("textType", textType, Field.Store.YES));
                document.add(new TextField("content", content, Field.Store.YES));
                document.add(new TextField("filename", f.getName(), Field.Store.YES));
                document.add(new SortedDocValuesField("fname", new BytesRef(f.getName())));
                document.add(new StringField("path", f.getAbsolutePath(), Field.Store.YES));
                int score = random.nextInt(600);
                document.add(new NumericDocValuesField("score_sort", score));
                document.add(new StoredField("score_store", score));

                writer.addDocument(document);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
