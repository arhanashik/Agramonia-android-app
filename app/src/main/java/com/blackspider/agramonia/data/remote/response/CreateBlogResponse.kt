package com.blackspider.agramonia.data.remote.response

import com.blackspider.agramonia.data.local.blog.Blog

data class CreateBlogResponse (val success: Boolean,
                               val message: String,
                               val blog: Blog)