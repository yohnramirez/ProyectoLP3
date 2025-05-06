package com.example.communitysecureapp.model.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("expires_at") val expiresAt: Long,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String,
    val user: User
)

data class User(
    val id: String,
    val aud: String,
    val role: String,
    val email: String,
    @SerializedName("email_confirmed_at") val emailConfirmedAt: String?,
    val phone: String,
    @SerializedName("confirmation_sent_at") val confirmationSentAt: String?,
    @SerializedName("confirmed_at") val confirmedAt: String?,
    @SerializedName("last_sign_in_at") val lastSignInAt: String?,
    @SerializedName("app_metadata") val appMetadata: AppMetadata,
    @SerializedName("user_metadata") val userMetadata: UserMetadata,
    val identities: List<Identity>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("is_anonymous") val isAnonymous: Boolean
)

data class AppMetadata(
    val provider: String,
    val providers: List<String>
)

data class UserMetadata(
    val email: String,
    val email_verified: Boolean,
    val phone_verified: Boolean,
    val sub: String
)

data class Identity(
    val identity_id: String,
    val id: String,
    val user_id: String,
    val identity_data: IdentityData,
    val provider: String,
    val last_sign_in_at: String,
    val created_at: String,
    val updated_at: String,
    val email: String
)

data class IdentityData(
    val email: String,
    val email_verified: Boolean,
    val phone_verified: Boolean,
    val sub: String
)