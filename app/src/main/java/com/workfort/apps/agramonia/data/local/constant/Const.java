package com.workfort.apps.agramonia.data.local.constant;

public interface Const {
    // Invalid
    int INVALID_INTEGER = -1;

    // Default
    String DEFAULT_STRING = "";
    boolean DEFAULT_BOOLEAN = false;
    int DEFAULT_INTEGER = 0;

    // Directory
    String DIRECTORY_ROOT = "/Agromonia/";

    // Prefix
    String PREFIX_IMAGE = "IMG_";

    // Postfix
    String SUFFIX_IMAGE = ".jpg";

    interface Action{
        String CREATE_BLOG = "com.blackspider.agramonia.create_blog";
    }

    interface RequestCode {
        int PIC_USER_PHOTO = 671;
        int CREATE_BLOG = 672;
    }

    interface Key {
        String FARMER = "farmer";
        String FARMER_ID = "farmer_id";
        String BLOG = "blog";
    }

    interface Language {
        String ENGLISH = "English";
        String ROMANIAN = "Romanian";
        String GERMANY = "German";
    }

    interface LanguageCode {
        String ENGLISH = "en";
        String ROMANIAN = "ro";
        String GERMANY = "de";
    }
}
