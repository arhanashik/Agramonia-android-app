package com.blackspider.agramonia.data.constant;

import android.os.Environment;

import java.io.File;

public interface AppConstants {
    // Invalid
    int INVALID_INTEGER = -1;

    // Default
    String DEFAULT_STRING = "";
    boolean DEFAULT_BOOLEAN = false;
    int DEFAULT_INTEGER = 0;

    // Directory
    String DIRECTORY_EXTERNAL_STORAGE =
            Environment.getExternalStorageDirectory() + File.separator;
    String DIRECTORY_ROOT = DIRECTORY_EXTERNAL_STORAGE + "/Agromonia";
    String DIRECTORY_IMAGE = DIRECTORY_EXTERNAL_STORAGE + DIRECTORY_ROOT + "/Image/";

    // Prefix
    String PREFIX_IMAGE = "Agromonia_IMG_";

    // Postfix
    String POSTFIX_IMAGE = ".jpg";
}
