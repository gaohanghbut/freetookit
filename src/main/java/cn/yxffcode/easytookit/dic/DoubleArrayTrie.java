package cn.yxffcode.easytookit.dic;

import cn.yxffcode.easytookit.collection.IntIterator;
import cn.yxffcode.easytookit.collection.IntStack;
import cn.yxffcode.easytookit.lang.IntsRef;
import cn.yxffcode.easytookit.lang.StringIntsRef;
import com.google.common.base.Function;
import com.sun.istack.internal.NotNull;

import static cn.yxffcode.easytookit.utils.ArrayUtils.grow;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 双数组字典树,使用字节数组实现
 * <p/>
 * 实现原理:
 * base[s] + c = t;
 * check[t] = s;
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
    public void add(@NotNull String word) {
        checkNotNull(word);
        IntsRef intsRef = checkNotNull(intsRefTransformer.apply(word + END_INPUT));
        for (int i = 0, j = intsRef.length(), s = INIT_STATE; i < j; i++) {
            if (s >= base.length) {
                base = grow(base, s * 2);
            }
            int elem = intsRef.element(i);
            int t    = base[s] + elem;
            if (t >= check.length) {
                check = grow(check, t * 2);
            }
            if (check[t] != NONE && check[t] != s) {
                //冲突,需要重新分配base[s]
                IntStack children = new IntStack();
                for (int k = 0; k < check.length; k++) {
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
    public boolean match(@NotNull String word) {
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
            return new StringIntsRef(input);
        }
    }
}
