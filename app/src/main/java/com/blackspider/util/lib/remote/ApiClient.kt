package com.blackspider.util.lib.remote

import com.blackspider.agramonia.data.local.blog.Blog
import com.blackspider.agramonia.data.remote.response.*
import io.reactivex.Observable
import retrofit2.http.*
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Multipart

interface ApiClient {
    @POST("api/login")
    fun login(@Query("email") email: String,
              @Query("password") password: String):
            Observable<LoginResponse>

    @POST("api/registration")
    fun registration(@Query("name") name: String,
                     @Query("profile_image") image: String,
                     @Query("location") location: String,
                     @Query("phone") phone: String,
                     @Query("email") email: String,
                     @Query("password") password: String,
                     @Query("answers") answers: HashMap<String, String>,
                     @Query("images") images: List<String>):
            Observable<RegistrationResponse>

    @Multipart
    @POST("uploader/Api.php?call=upload")
    fun uploadImage(@Part file: MultipartBody.Part): Observable<ImageUploadResponse>

    @Multipart
    @POST("uploader/Api.php?call=multiple_upload")
    fun uploadMultipleImage(@Part files: List<MultipartBody.Part>):
            Observable<MultipleImageUploadResponse>

    @GET("api/blog-list")
    fun getBlogList(@Query("user_id") userId: Int) : Observable<List<Blog>>

    @POST("api/blog/store")
    fun createBlog(@Query("title") title: String,
                     @Query("description") description: String,
                     @Query("user_id") userId: Int,
                     @Query("images") images: List<String>):
            Observable<CreateBlogResponse>
}