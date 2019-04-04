package com.workfort.apps.util.lib.remote

import com.workfort.apps.agramoniaapp.data.local.blog.BlogEntity
import com.workfort.apps.agramoniaapp.data.remote.response.*
import io.reactivex.Observable
import retrofit2.http.*
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Multipart

interface ApiService {
    @FormUrlEncoded
    @POST("api/login")
    fun login(@Field("phone") phone: String): Observable<LoginResponse>

    @FormUrlEncoded
    @POST("api/registration")
    fun registration(@Field("name") name: String,
                     @Field("profile_image") image: String,
                     @Field("location") location: String,
                     @Field("phone") phone: String,
                     @Field("answers_ro") answersRo: String,
                     @Field("answers_de") answersDe: String,
                     @Field("answers_en") answersEn: String,
                     @Field("answer_images") answerImages: String,
                     @Field("images[]") images: List<String>):
            Observable<RegistrationResponse>

    @Multipart
    @POST("uploader/Api.php?call=upload")
    fun uploadImage(@Part file: MultipartBody.Part): Observable<ImageUploadResponse>

    @Multipart
    @POST("uploader/Api.php?call=multiple_upload")
    fun uploadMultipleImage(@Part files: List<MultipartBody.Part>):
            Observable<MultipleImageUploadResponse>

    @GET("api/blog-list")
    fun getBlogList(@Query("user_id") userId: Int) : Observable<List<BlogEntity>>

    @FormUrlEncoded
    @POST("api/create-blog")
    fun createBlog(@Field("title") title: String,
                   @Field("description") description: String,
                   @Field("user_id") userId: Int,
                   @Field("images[]") images: List<String>):
            Observable<CreateBlogResponse>

    @FormUrlEncoded
    @POST("api/create-service")
    fun createService(@Field("title") title: String,
                   @Field("price") price: Int,
                   @Field("family_id") familyId: Int):
            Observable<CreateServiceResponse>
}