package com.workfort.apps.agramonia.data.remote.response

import com.workfort.apps.agramonia.data.local.blog.BlogEntity

data class CreateBlogResponse (val success: Boolean,
                               val message: String,
                               val blog: BlogEntity)