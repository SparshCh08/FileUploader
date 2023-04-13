package com.reactive.uploadFiles.controllers

import com.reactive.uploadFiles.Services.UploadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicLong


@RestController
class UploadController {

    @Autowired
    lateinit var uploadService: UploadService

    @PostMapping("single/upload")
    fun uploadFileWithoutId(
        @RequestPart("file") files: Flux<FilePart>, @RequestHeader("content-length") contentLength: Long
    ): Mono<ResponseEntity<String>> {
//        print(files.count().toString())
//        print(files.count())
        return uploadService.uploadfile(files, contentLength, null)
    }


    @PostMapping("single/upload/{id}")
    fun uploadFileWithId(
        @RequestPart("file") files: Flux<FilePart>, @RequestHeader("content-length") contentLength: Long, @PathVariable id : String
    ): Mono<ResponseEntity<String>> {
        return uploadService.uploadfile(files, contentLength, id)
    }
}