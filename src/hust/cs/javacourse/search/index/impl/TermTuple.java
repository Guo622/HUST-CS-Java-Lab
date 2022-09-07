package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.AbstractTermTuple;

public class TermTuple extends AbstractTermTuple {

    public TermTuple() {
    }

    public TermTuple(AbstractTerm term, int curPos) {
        this.term = new Term(term);
        this.curPos = curPos;
    }

    /**
     * 判断二个三元组内容是否相同
     *
     * @param obj ：要比较的另外一个三元组
     * @return 如果内容相等（三个属性内容都相等）返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TermTuple) {
            TermTuple o = (TermTuple) obj;
            return this.term.equals(o.term) && this.curPos == o.curPos;  //freq为常量，无须比较
        }
        return false;
    }

    /**
     * 获得三元组的字符串表示
     *
     * @return ： 三元组的字符串表示
     */
    @Override
    public String toString() {
        return "(TermTuple) { term: " + term + " , freq: " + freq + " , curPos: " + curPos + " }";
    }

    public static void main(String[] args) {
        Term term = new Term("java");
        TermTuple termTuple = new TermTuple(term, 2);
        System.out.println(termTuple);
    }
}
