package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Term extends AbstractTerm {

    public Term() {
    }

    public Term(String content) {
        this.content = content;
    }

    public Term(AbstractTerm term) {
        this.content = term.getContent();
    }

    /**
     * 判断二个Term内容是否相同
     *
     * @param obj ：要比较的另外一个Term
     * @return 如果内容相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Term) {
            Term o = (Term) obj;
            if (content == null && o.content != null || content != null && o.content == null)
                return false;
            return content == null || content.equals(o.content);  //均为null或均不为null
        }
        return false;
    }

    /**
     * 返回Term的字符串表示
     *
     * @return 字符串
     */
    @Override
    public String toString() {
        return content;
    }

    /**
     * 返回Term内容
     *
     * @return Term内容
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * 设置Term内容
     *
     * @param content ：Term的内容
     */
    @Override
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 比较二个Term大小（按字典序）
     *
     * @param o ： 要比较的Term对象
     * @return ： 返回二个Term对象的字典序差值
     */
    @Override
    public int compareTo(AbstractTerm o) {
        return this.content.compareTo(o.getContent());
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this);     //直接将this整个对象的内容序列化
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
            Term o = (Term) (in.readObject());  //与writeObject对应，直接读出整个对象
            this.setContent(o.content);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
