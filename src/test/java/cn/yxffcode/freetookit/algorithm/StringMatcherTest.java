package cn.yxffcode.freetookit.algorithm;

/**
 * @author gaohang on 16/7/29.
 */
public class StringMatcherTest {

    public void testCharsMatcher() {
        CharSequenceMatcher charsMatcher = CharSequenceMatcher.create("hello").buildNextIfAbsent();
        {
            int idx = charsMatcher.indexOf("hello world");
        }
        {
            int idx = charsMatcher.indexOf("hello everybody");
        }
    }
}
