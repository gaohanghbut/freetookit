package cn.yxffcode.easytookit.lang;

/**
 * 可配置的对象
 *
 * @author gaohang on 2014/8/24.
 */
public interface Configurable<Configuration> {

    /**
     * 查找配置
     */
    Configuration lookupConfig(String name);
}
