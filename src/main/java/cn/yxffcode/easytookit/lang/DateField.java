package cn.yxffcode.easytookit.lang;

import java.util.Calendar;

/**
 * 表示日期的域(年,月,日)的枚举
 *
 * @author gaohang on 15/11/29.
 */
public enum DateField {
    YEAR {
        @Override
        public int getCalendarField() {
            return Calendar.YEAR;
        }
    },
    MOUTH {
        @Override
        public int getCalendarField() {
            return Calendar.MONTH;
        }
    },
    DAY {
        @Override
        public int getCalendarField() {
            return Calendar.DAY_OF_MONTH;
        }
    };

    public abstract int getCalendarField();
}
