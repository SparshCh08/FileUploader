package com.reactive.uploadFiles.controllers

import com.reactive.uploadFiles.Services.DownloadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.io.IOException


@RestController
class DownloadController {

    @Autowired
    lateinit var service: DownloadService
    @GetMapping("/download/{id}/{fileName}")
    @Throws(IOException::class)
    fun downloadFile( @PathVariable id: String, @PathVariable fileName: String) : Mono<ResponseEntity<Resource>> {
        return service.downloadFile(id, fileName)
    }

}