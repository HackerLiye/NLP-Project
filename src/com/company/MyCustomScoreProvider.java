package com.company;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;


public class MyCustomScoreProvider extends CustomScoreProvider {

        public MyCustomScoreProvider(LeafReaderContext context) {
            super(context);
        }

        @Override
        public float customScore(int doc, float subQueryScore, float valSrcScore) {
            return subQueryScore * valSrcScore;
        }
}
