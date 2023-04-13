package com.reactive.uploadFiles.Services

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class DownloadService {

    fun downloadFile(id: String, filename: String): Mono<ResponseEntity<Resource>> {

        val basePath: Path = Paths.get("./src/main/resources/files/${id}")
        val filePath: Path = basePath.toAbsolutePath().normalize().resolve(filename)
        val filesFoundOrNot = !Files.exists(filePath)
//        print(filePath)

        return Mono.fromCallable {
            when {
                filesFoundOrNot -> throw FileNotFoundException()
                else -> {
                    val resource: Resource = UrlResource(filePath.toUri())
                    val httpHeaders = HttpHeaders()
                    httpHeaders.add("File-Name", filename)
                    httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=" + resource.filename)
                    ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .headers(httpHeaders).body(resource)
                }
            }
        }
    }
}