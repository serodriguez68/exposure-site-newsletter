package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases

import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.repositories.SuburbRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetWatchableSuburbs(
    @Autowired val suburbRepo: SuburbRepository
) {
    fun call(): List<SuburbDTO> = suburbRepo.findAll().map { SuburbDTO.fromSuburb(it) }
}