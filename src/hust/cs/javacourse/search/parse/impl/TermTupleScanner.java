package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TermTupleScanner extends AbstractTermTupleScanner {

    private int count = 0;  //当前读取单词个数
    private final List<String> words = new ArrayList<>();

    public TermTupleScanner() {
    }

    public TermTupleScanner(BufferedReader input) {
        super(input);
    }

    /**
     * 获得下一个三元组
     *
     * @return : 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        if (count == words.size()) {  //读取个数等于words大小时再从文件中读取一行单词
            try {
                List<String> newWords = null;
                do {
                    String line = input.readLine();
                    StringSplitter splitter = new StringSplitter();
                    splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
                    if (line == null) {  //文件读取结束返回null
                        close();
                        return null;
                    }
                    newWords = splitter.splitByRegex(line.toLowerCase(Locale.ROOT));
                } while (newWords.size() <= 0);
                words.addAll(newWords);  //将新一行的单词添加至words中
                return new TermTuple(new Term(words.get(count)), count++); //返回一个单词的termTuple
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return new TermTuple(new Term(words.get(count)), count++);
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("text/1.txt"))));
        AbstractTermTupleScanner abstractTermTupleScanner = new TermTupleScanner(bufferedReader);
        AbstractTermTuple termTuple = abstractTermTupleScanner.next();
        while (termTuple != null) {
            System.out.println(termTuple);
            termTuple = abstractTermTupleScanner.next();
        }
    }
}
