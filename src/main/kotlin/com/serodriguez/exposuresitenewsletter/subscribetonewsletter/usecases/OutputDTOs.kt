package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.entities.Suburb
import java.util.UUID

data class SuburbDTO(
    val id: UUID,
    val name: String,
    val postCode: String
) {
    companion object {
        fun fromSuburb(suburb: Suburb) =
            SuburbDTO(
                id= suburb.id!!,
                name = suburb.name,
                postCode = suburb.postCode
            )
    }
}