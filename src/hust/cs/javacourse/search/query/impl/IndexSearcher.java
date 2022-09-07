package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.PostingList;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        index.load(new File(indexFile));
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        PostingList postingList = (PostingList) index.search(queryTerm);  //获得term对应的postingList
        List<AbstractHit> hits = new ArrayList<>();
        Map<AbstractTerm, AbstractPosting> m;
        for (int i = 0; i < postingList.size(); i++) {
            AbstractPosting p = postingList.get(i);  //获得一个posting
            m = new TreeMap<>();
            m.put(queryTerm, p);
            AbstractHit hit = new Hit(p.getDocId(), index.getDocName(p.getDocId()), m);
            hit.setScore(sorter.score(hit));
            hits.add(hit);
        }
        sorter.sort(hits);
        AbstractHit[] hitArray = new AbstractHit[hits.size()];
        return hits.toArray(hitArray);
    }

    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        //获得两个term的postingList
        PostingList postingList1 = (PostingList) index.search(queryTerm1);
        PostingList postingList2 = (PostingList) index.search(queryTerm2);
        List<AbstractHit> hits = new ArrayList<>();
        Map<AbstractTerm, AbstractPosting> m;
        // 或包括与所以先求与的结果
        for (int i = 0; i < postingList1.size(); i++) {
            AbstractPosting p1 = postingList1.get(i);  //获得term1的一个posting
            int j = postingList2.indexOf(p1.getDocId());
            if (j >= 0) {
                AbstractPosting p2 = postingList2.get(j); //获得与p1相同文章的term2的posting
                if (combine == LogicalCombination.Phrase) { //如果为查找短语
                    int flag = 0;
                    List<Integer> poslist1 = p1.getPositions();  //获取两个term的posList
                    List<Integer> poslist2 = p2.getPositions();
                    for (int pos : poslist1) {
                        if (poslist2.contains(pos - 1) || poslist2.contains(pos + 1)) {
                            flag = 1;   //如果两个posList中有pos相差1，即相邻，则存在短语
                            break;
                        }
                    }
                    if (flag == 0) //如果未找到则直接下一个循环
                        continue;
                }
                m = new TreeMap<>();
                m.put(queryTerm1, p1);
                m.put(queryTerm2, p2);
                AbstractHit hit = new Hit(p1.getDocId(), index.getDocName(p1.getDocId()), m);
                hit.setScore(sorter.score(hit));
                hits.add(hit);   //添加包含term1和term2的一个hit
            }
            if (combine == LogicalCombination.OR) { //如果为或则单独添加一个含term1的hit,含term2的在之后单独添加
                m = new TreeMap<>();
                m.put(queryTerm1, p1);
                AbstractHit hit = new Hit(p1.getDocId(), index.getDocName(p1.getDocId()), m);
                hit.setScore(sorter.score(hit));
                hits.add(hit);
            }
        }
        if (combine == LogicalCombination.OR)
            hits.addAll(List.of(search(queryTerm2, sorter))); //添加所有含term2的hit
        sorter.sort(hits);
        AbstractHit[] hitArray = new AbstractHit[hits.size()];
        return hits.toArray(hitArray);
    }

}
