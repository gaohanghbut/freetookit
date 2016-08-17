package cn.yxffcode.freetookit.text;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

/**
 * @author gaohang on 16/8/17.
 */
public class TextTemplateTest {

    private final TextTemplate textTemplate = new TextTemplate("[#{title}]您的#{money}元资产出错了,请在1分钟内提现,否则,资产将无法赎回.");

    @Test
    public void testRend() throws Exception {

        final Map<String, Object> context = Maps.newHashMap();
        context.put("title", "灵机");
        context.put("money", 1000000000);

        System.out.println(textTemplate.rend(context));

        System.out.println(textTemplate.rend(new Context("灵玑", 1000000000)));
    }

    public static final class Context {
        private final String title;
        private final double money;

        public Context(final String title, final double money) {
            this.title = title;
            this.money = money;
        }
    }
}
