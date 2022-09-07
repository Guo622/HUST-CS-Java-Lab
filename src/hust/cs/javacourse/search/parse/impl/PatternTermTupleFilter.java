package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

import java.io.*;

public class PatternTermTupleFilter extends AbstractTermTupleFilter {
    /**
     * 构造函数
     *
     * @param input ：Filter的输入，类型为AbstractTermTupleStream
     */
    public PatternTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    /**
     * 获得下一个三元组
     *
     * @return : 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple termTuple = input.next();
        while (termTuple != null) {
            String content = termTuple.term.getContent();
            if (content.matches(Config.TERM_FILTER_PATTERN)) break;
            else termTuple = input.next();
        }
        return termTuple;
    }

    public static void main(String[] args) throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("text/1.txt"))));
        AbstractTermTupleScanner abstractTermTupleScanner = new TermTupleScanner(bufferedReader);
        AbstractTermTupleFilter patternfilter = new PatternTermTupleFilter(abstractTermTupleScanner);
        AbstractTermTuple termTuple = patternfilter.next();
        while (termTuple != null) {
            System.out.println(termTuple);
            termTuple = patternfilter.next();
        }
    }
}
