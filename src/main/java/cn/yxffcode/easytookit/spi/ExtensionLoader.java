package cn.yxffcode.easytookit.spi;

import java.util.List;

/**
 * 最初出现在 https://github.com/gaohanghbut/http (用来练手的项目)中,用于加载SPI
 * Created by hang.gao on 2015/6/9.
 */
public interface ExtensionLoader<T> {
  List<T> getExtensions();

  T getExtension();
}
