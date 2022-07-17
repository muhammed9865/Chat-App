package com.muhammed.chatapp.data

sealed class NetworkExceptions : Exception() {
    data class NoCommunitiesFoundException(override val message: String = "No communities found") :
        NetworkExceptions()

}