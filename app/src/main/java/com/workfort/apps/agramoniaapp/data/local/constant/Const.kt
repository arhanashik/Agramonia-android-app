package com.workfort.apps.agramoniaapp.data.local.constant

object Const {
    // Invalid
    const val INVALID_INTEGER = -1

    // Default
    const val DEFAULT_STRING = ""
    const val DEFAULT_BOOLEAN = false
    const val DEFAULT_INTEGER = 0

    // Directory
    const val DIRECTORY_ROOT = "/Agramonia/"

    // Prefix
    object Prefix{
        const val IMAGE = "IMG_"
        const val FAMILY = "FAMILY_"
        const val QA = "QA_"
        const val SERVICE = "SERVICE_"
        const val PROPOSAL = "PROPOSAL_"
        const val BLOG = "BLOG_"
    }


    // Postfix
    const val SUFFIX_IMAGE = ".jpg"

    object Action{
        const val CREATE_BLOG = "com.blackspider.agramonia.create_blog"
    }

    object RequestCode {
        const val PIC_USER_PHOTO = 671
        const val PIC_FAMILY_PHOTO = 672
        const val PIC_ANSWER_PHOTO = 673
        const val PIC_PROPOSAL_PHOTO = 674

        const val CREATE_BLOG = 771
    }

    object Key {
        const val FARMER = "family"
        const val FARMER_ID = "farmer_id"
        const val BLOG = "blog"
    }

    object Language {
        const val ENGLISH = "English"
        const val ROMANIAN = "Romanian"
        const val GERMANY = "German"
    }

    object LanguageCode {
        const val ENGLISH = "en"
        const val ROMANIAN = "ro"
        const val GERMANY = "de"
    }
}
