package cn.yxffcode.easytookit.dic;

import cn.yxffcode.easytookit.collection.IntIterator;
import cn.yxffcode.easytookit.collection.IntStack;
import cn.yxffcode.easytookit.lang.IntsRef;
import cn.yxffcode.easytookit.lang.StringIntsRef;
import com.google.common.base.Function;

import static cn.yxffcode.easytookit.utils.ArrayUtils.grow;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 双数组字典树,使用int数组实现
 * <p/>
 * 实现原理:
 * base[s] + c = t;
 * check[t] = s;
 * <p/>
 * 1.base与check是两个平行的数组,数组的下标表示状态
 * 2.base数组与check数组表示节点之间的父子关系
 * 3.base[s]的值表示状态s的子节点在check数组中的起始下标(但起始下标不一定存的是s的子节点)
 * 4.check[t]表示状态t的父节点状态
 * 4.输入c后,状态由s转成t,新的状态,那么check[t]=s,状态s增加一个子节点base[s] + c且满足关系t = base[s] + c
 * <p/>
 * 创建过程:
 * base数组的起始值都为0,初始状态为1,check数组的初始值为0,表示没有父节点
 * 输入字符c,则t = base[s] + c,检查check[t]的值,如果check[t]==s,则表示此字符在公共前缀中,状态转换成t,如果check[t]==0
 * 表示t这个状态还没有被加入到字典中,则check[t]=s,状态转换成t,如果check[t]!=0且check[t]!=s,则表示check[t]已经被其它状态
 * 占用,那么修改base[s]的值为b,使修改前,所有的check[b + m]=0(check数组元素没有被其它节点占用)
 * 假设s状态后有输入为n,那么修改后check[b+n] = check[base[s] + n],base[b + n] = base[base[s] + n],然后修改base[s]+n
 * 的所有子节点,使用其check值为b+n.
 *
 * base       check
 * -------    -------
 * |     |    |     |
 * -------    -------
 * |     |s   |     | s
 * -------    -------
 * |     |t   |  s  | t
 * -------    -------
 * |     |    |     |
 * -------    -------
 * |     |    |  t  | m
 * -------    -------
 * |     |    |     |
 * -------    -------
 * |     |    |     |
 * -------    -------
 * 如果b=1,则base[s]由0变为1,那么t的值则变成t+1,那么check[t+1]=check[t],check[t]=0
 *                            base[t]变成base[t+1],base[t+1]=base[t]
 *                            t的子节点m也发生变动check[m]=t+1
 *                            最后base[s]=1
 *
 * @author gaohang on 15/12/9.
 */
public class DoubleArrayTrie implements Dictionary {
    private static final int INIT_ARRAY_SIZE = 50;
    private static final int INIT_STATE      = 1;
    private static final int END_INPUT       = '#';
    /**
     * 默认的check值
     */
    private static final int NONE            = 0;

    public static DoubleArrayTrie create(Iterable<String> words) {
        DoubleArrayTrie trie = new DoubleArrayTrie(DefaultIntsRefCreator.INSTANCE);
        for (String word : words) {
            trie.add(word);
        }
        return trie;
    }

    public static DoubleArrayTrie create(Iterable<String> words,
                                         Function<String, IntsRef> intsRefTransformer) {
        DoubleArrayTrie trie = new DoubleArrayTrie(intsRefTransformer);
        for (String word : words) {
            trie.add(word);
        }
        return trie;
    }

    private int[] base;

    private int[] check;

    private final Function<String, IntsRef> intsRefTransformer;

    private DoubleArrayTrie(final Function<String, IntsRef> intsRefTransformer) {
        this.intsRefTransformer = intsRefTransformer;
        this.base = new int[INIT_ARRAY_SIZE];
        this.check = new int[INIT_ARRAY_SIZE];
    }

    /**
     * 添加一个词条
     */
    public void add(String word) {
        checkNotNull(word);
        IntsRef intsRef = checkNotNull(intsRefTransformer.apply(word + END_INPUT));
        for (int i = 0, j = intsRef.length(), s = INIT_STATE; i < j; i++) {
            if (s >= base.length) {
                base = grow(base, s * 2);
            }
            int elem = intsRef.element(i);
            if (elem <= 0) {
                continue;
            }
            int t = base[s] + elem;
            if (t >= check.length) {
                check = grow(check, t * 2);
            }
            if (check[t] != NONE && check[t] != s) {
                //冲突,需要重新分配base[s]
                IntStack children = new IntStack();
                for (int k = base[s]; k < check.length; k++) {
                    int e = check[k];
                    if (e == s) {
                        children.push(k - base[s]);
                    }
                }

                int b = 1;
                base:
                for (; ; b++) {//b表示新的base[s]
                    for (IntIterator iterator = children.iterator(); iterator.hasNext(); ) {
                        //t = base[s] + c,则c = t - base[s],此时t是child
                        int c  = iterator.next();
                        int nt = b + c;
                        if (nt >= check.length) {
                            check = grow(check, nt * 2);
                        }
                        if (check[nt] != NONE) {
                            continue base;
                        }
                    }
                    if (check[b + elem] == NONE) {
                        break;
                    }
                }
                //找到了满足条件的b,做节点的转移
                for (IntIterator iterator = children.iterator(); iterator.hasNext(); ) {
                    int child = iterator.next();
                    //base[s] + child = t;
                    int nt = b + child;
                    int ot = child + base[s];
                    check[nt] = check[ot];
                    check[ot] = NONE;
                    base[nt] = base[ot];

                    for (int k = 0, m = check.length; k < m; k++) {
                        if (check[k] == ot) {
                            check[k] = nt;
                        }
                    }
                }
                base[s] = b;
                t = base[s] + elem;
            }
            check[t] = s;
            s = t;
        }
    }

    @Override
    public boolean match(String word) {
        checkNotNull(word);
        IntsRef intsRef = checkNotNull(intsRefTransformer.apply(word + END_INPUT));
        for (int i = 0, j = intsRef.length(), s = INIT_STATE; i < j; i++) {
            if (s >= base.length) {
                return false;
            }
            int c = intsRef.element(i);
            int t = base[s] + c;
            if (t >= check.length) {
                return false;
            }
            if (t >= check.length) {
                return false;
            }
            if (check[t] == s) {
                s = t;
            } else {
                return false;
            }
        }
        return true;
    }

    private enum DefaultIntsRefCreator implements Function<String, IntsRef> {
        INSTANCE;

        @Override
        public IntsRef apply(final String input) {
            return new IntsRefWrapper(new StringIntsRef(input));
        }
    }

    private static final class IntsRefWrapper implements IntsRef {
        /**
         * 中文字符的起始数字
         */
        private static final int CN_CHAR_FIRST = 19968;
        /**
         * 中文字符的最大字符数字
         */
        private static final int CN_CHAR_LAST  = 171941;

        private static final int END_INPUT_VALUE  = 1;
        private static final int DIGIT_START      = 2;
        private static final int LOW_CASE_START   = 12;
        private static final int UPPER_CASE_START = 38;
        private static final int CN_CHAR_START    = 74;

        private final IntsRef delegate;

        private IntsRefWrapper(final IntsRef delegate) {
            this.delegate = delegate;
        }

        @Override
        public int element(final int index) {
            int elem = delegate.element(index);
            if (elem >= 'a' && elem <= 'z') {
                return LOW_CASE_START + (elem - 'a');
            } else if (elem >= 'A' && elem <= 'Z') {
                return UPPER_CASE_START + (elem - 'A');
            } else if (elem >= CN_CHAR_FIRST && elem <= CN_CHAR_LAST) {
                return CN_CHAR_START + (elem - CN_CHAR_FIRST);
            } else if (elem >= '0' && elem <= '9') {
                return DIGIT_START + (elem - '0');
            } else if (elem == '#') {
                return END_INPUT_VALUE;
            }
            //不接受此字符
            return - 1;
        }

        @Override
        public int length() {
            return delegate.length();
        }

        @Override
        public int compareTo(final IntsRef o) {
            return delegate.compareTo(o);
        }
    }
}
