package com.haishanda.android.videoapp.Utils;

import java.io.File;

/**
 * Created by Zhongsz on 2016/11/21.
 */

public class FileUtil {
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (File childFile : childFiles) {
                delete(childFile);
            }
            file.delete();
        }
    }
}
