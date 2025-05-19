package com.example.communitysecureapp.service

import com.example.communitysecureapp.model.comment.CommentRequest
import com.example.communitysecureapp.model.comment.CommentResponse
import com.example.communitysecureapp.model.document.TypeDocument
import com.example.communitysecureapp.model.gender.Gender
import com.example.communitysecureapp.model.login.LoginRequest
import com.example.communitysecureapp.model.login.LoginResponse
import com.example.communitysecureapp.model.login.User
import com.example.communitysecureapp.model.register.RegisterRequest
import com.example.communitysecureapp.model.register.RegisterResponse
import com.example.communitysecureapp.model.report.ReportHistoryResponse
import com.example.communitysecureapp.model.report.ReportRequest
import com.example.communitysecureapp.model.report.ReportResponse
import com.example.communitysecureapp.model.type.ReportType
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /*
        AUTH
     */
    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse


    /*
        USERS
     */
    @GET("api/user/search/{id}")
    suspend fun getUserById(@Path("id") id: String): User


    /*
        TYPE DOCUMENTS
     */
    @GET("/api/type-documents/")
    suspend fun getTypesDocument(): List<TypeDocument>

    @GET("/api/type-documents/{id}")
    suspend fun getOneTypeDocument(@Path("id") id: Long): TypeDocument

    @POST("/api/type-documents/create")
    suspend fun createTypeDocument(@Body typeDocument: TypeDocument): TypeDocument

    @PUT("/api/type-documents/{id}")
    suspend fun updateTypeDocument(
        @Path("id") id: Long,
        @Body updateData: TypeDocument
    ): TypeDocument


    /*
        GENDER
     */
    @GET("/api/genders/")
    suspend fun getGenders(): List<Gender>

    @GET("/api/genders/{id}")
    suspend fun getOneGender(@Path("id") id: Long): Gender

    @POST("/api/genders/create")
    suspend fun createGender(@Body gender: Gender): Gender

    @PUT("/api/genders/{id}")
    suspend fun updateGender(@Path("id") id: Long, @Body updateData: Gender): Gender


    /*
        TYPE REPORTS
     */
    @POST("/api/report-types/create")
    suspend fun createTypeReport(@Body reportType: ReportType): ReportType

    @GET("/api/report-types/")
    suspend fun getReportTypes(): List<ReportType>

    @GET("/api/report-types/{id}")
    suspend fun getOneReportType(@Path("id") id: Long): ReportType

    @PUT("/api/report-types/{id}")
    suspend fun updateReportType(@Path("id") id: Long, @Body updateData: ReportType)

    @DELETE("/api/report-types/{id}")
    suspend fun deleteReportType(@Path("id") id: Long)


    /*
        REPORTS
     */
    @POST("/api/reports/create")
    suspend fun createReport(@Body report: ReportRequest): ReportResponse

    @GET("api/reports/nearby")
    suspend fun getNearByReports(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("rad") radius: Double
    ): List<ReportResponse>

    @GET("api/reports/user/{id}")
    suspend fun getReportsByUserId(
        @Path("id") userId: String
    ): List<ReportResponse>

    @GET("api/reports/my-reports")
    suspend fun getMyReports(
        @Header("Authorization") authToken: String
    ): List<ReportResponse>

    @PATCH("api/reports/{id}/status")
    suspend fun updateReportStatus(
        @Path("id") reportId: Long,
        @Query("status") newStatus: String
    ): ReportResponse

    @GET("api/reports/status")
    suspend fun getReportsByStatus(@Query("status") status: String): List<ReportResponse>

    @GET("api/reports/{id}/status-history")
    suspend fun getReportStatusHistory(@Path("id") reportId: Long): List<ReportHistoryResponse>


    /*
        REPORT COMMENTS
     */
    @POST("api/reports-comment/create")
    suspend fun createComment(@Body comment: CommentRequest): CommentResponse

    @GET("api/reports-comment/report/{id}")
    suspend fun getCommentsByReportId(@Path("id") id: Long): List<CommentResponse>
}