package com.blackspider.agramonia.data.remote.response

import com.blackspider.agramonia.data.local.blog.BlogEntity

data class CreateBlogResponse (val success: Boolean,
                               val message: String,
                               val blog: BlogEntity)