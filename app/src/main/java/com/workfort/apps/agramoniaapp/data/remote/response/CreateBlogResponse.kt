package com.workfort.apps.agramoniaapp.data.remote.response

import com.workfort.apps.agramoniaapp.data.local.blog.BlogEntity

data class CreateBlogResponse (val success: Boolean,
                               val message: String,
                               val blog: BlogEntity)