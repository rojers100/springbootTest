package com.luojie.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtil {

    public static ResourceBundle getInstance(Locale language) {
            if (language != null) {
                // 这里的message是指文件的前缀(baseName)
                // 国际化的命名规则是baseName_local.properties
              return ResourceBundle.getBundle("message", language);
            }
        return ResourceBundle.getBundle("message");
    }
}
