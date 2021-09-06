package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.web

import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SuburbData
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SuburbConverter: Converter<String, SuburbData> {
    override fun convert(stringId: String): SuburbData {
        return SuburbData(UUID.fromString(stringId))
    }
}