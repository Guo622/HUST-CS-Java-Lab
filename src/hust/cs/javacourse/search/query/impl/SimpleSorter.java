package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SimpleSorter implements Sort {
    @Override
    public void sort(List<AbstractHit> hits) {
        Collections.sort(hits);
    }

    @Override
    public double score(AbstractHit hit) {
        //频率越大优先级越高,如果为与的hit包含两个term则取平均
        int freq = 0;
        for (Map.Entry<AbstractTerm, AbstractPosting> entry : hit.getTermPostingMapping().entrySet())
            freq += entry.getValue().getFreq();
        return -1.0 * freq;
    }
}
