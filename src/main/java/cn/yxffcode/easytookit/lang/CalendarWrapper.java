package cn.yxffcode.easytookit.lang;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 对{@link Calendar}类的常用功能的包装
 *
 * @author gaohang on 15/11/29.
 */
public class CalendarWrapper {
    private final Calendar delegate;

    private CalendarWrapper(final Calendar delegate) {
        this.delegate = delegate;
    }

    public static CalendarWrapper getInstance() {
        return new CalendarWrapper(Calendar.getInstance());
    }

    public static CalendarWrapper getInstance(final TimeZone zone) {
        return new CalendarWrapper(Calendar.getInstance(zone));
    }

    public static CalendarWrapper getInstance(final Locale aLocale) {
        return new CalendarWrapper(Calendar.getInstance(aLocale));
    }

    public static CalendarWrapper getInstance(final TimeZone zone, final Locale aLocale) {
        return new CalendarWrapper(Calendar.getInstance(zone, aLocale));
    }

    /**
     * 日期的加法运算,功能与{@link Calendar#add(int, int)}一样,但是Calendar.add方法的参数不明确,难以使用
     *
     * @param field 需要加的域
     * @param delta 增加的值
     */
    public void add(DateField field, int delta) {
        delegate.add(field.getCalendarField(), delta);
    }
}
