package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.*;


/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {


    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("(Index) {\n\tdictionarySize : " +
                getDictionary().size() + "\n\tdocIdToDocPathMapping : {\n");
        for (Map.Entry<Integer, String> entry : docIdToDocPathMapping.entrySet()) {
            s.append("\t\t(docId) ").append(entry.getKey()).append(" => (DocPath) \"").append(
                    entry.getValue()).append("\"\n");
        }
        s.append("\t}\n\ttermToPostingListMapping : {\n");
        for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
            s.append("\t\t").append(entry.getKey()).append(" => ").append(entry.getValue()).append("\n");
        }
        s.append("\t}\n}");
        return s.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        docIdToDocPathMapping.put(document.getDocId(), document.getDocPath());  // 添加docId->docPath映射
        List<AbstractTermTuple> tuples = document.getTuples();  //获得document所有term
        Map<AbstractTerm, List<Integer>> termToPositions = new TreeMap<>();  //构建term->positions映射
        for (AbstractTermTuple termTuple : tuples) {
            List<Integer> positions = termToPositions.get(termTuple.term);  //获得当前term的positions列表
            if (positions == null) { //为null表示该term第一次出现
                positions = new ArrayList<>();
                positions.add(termTuple.curPos);  //添加当前位置
                termToPositions.put(termTuple.term, positions); //加入映射
            } else positions.add(termTuple.curPos);
        }
        for (Map.Entry<AbstractTerm, List<Integer>> entry : termToPositions.entrySet()) {
            AbstractPostingList postingList = this.search(entry.getKey()); //获得当前term在index中的postingList
            if (postingList == null) { //为null表示index中不含该term
                postingList = new PostingList();  //构建新的postingList
                postingList.add(new Posting(document.getDocId(), entry.getValue().size(), entry.getValue())); //添加新的posting
                termToPostingListMapping.put(entry.getKey(), postingList); //添加新的term->postingList映射
            } else postingList.add(new Posting(document.getDocId(), entry.getValue().size(), entry.getValue()));
        }
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        try {
            this.readObject(new ObjectInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try {
            this.writeObject(new ObjectOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
            entry.getValue().sort();
//           int size=entry.getValue().size();
//           for(int i=0;i<size;i++){
//               entry.getValue().get(i).sort();
//           }
            //每个Posting里的positions在构建时已保证升序排列
        }


    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try {
            Index index = (Index) (in.readObject());
            this.docIdToDocPathMapping = index.docIdToDocPathMapping;
            this.termToPostingListMapping = index.termToPostingListMapping;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DocumentBuilder documentBuilder = new DocumentBuilder();
        AbstractDocument document = documentBuilder.build(1, "text/1.txt", new File("text/1.txt"));
        AbstractDocument document1 = documentBuilder.build(2, "text/2.txt", new File("text/2.txt"));
        AbstractIndex index = new Index();
        index.addDocument(document);
        index.addDocument(document1);
        System.out.println(index);
    }
}


