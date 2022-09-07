package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.*;
import java.util.List;

public class IndexBuilder extends AbstractIndexBuilder {
    public IndexBuilder(AbstractDocumentBuilder docBuilder) {
        super(docBuilder);
    }

    /**
     * <pre>
     * 构建指定目录下的所有文本文件的倒排索引.
     *      需要遍历和解析目录下的每个文本文件, 得到对应的Document对象，再依次加入到索引，并将索引保存到文件.
     * @param rootDirectory ：指定目录
     * @return ：构建好的索引
     * </pre>
     */
    @Override
    public AbstractIndex buildIndex(String rootDirectory) {
        AbstractIndex index = new Index();
        List<String> filePaths = FileUtil.list(rootDirectory, ".txt"); //获得.txt文件名
        for (String path : filePaths) {
            index.addDocument(docBuilder.build(docId++, path, new File(path)));
        }
        try {
            index.writeObject(new ObjectOutputStream(new FileOutputStream(new File("index/index.dat"))));
            System.out.println("\n索引已保存至 index/index.dat\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return index;
    }

    public static void main(String[] args) throws IOException {
        AbstractIndexBuilder indexBuilder = new IndexBuilder(new DocumentBuilder());
        indexBuilder.buildIndex("text");
        AbstractIndex index = new Index();
        index.readObject(new ObjectInputStream(new FileInputStream(new File("index/index.dat"))));
        System.out.println(index);
    }
}
