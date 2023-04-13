package com.reactive.uploadFiles.Services

import com.reactive.uploadFiles.exceptions.FileNotSelectException
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.util.unit.DataSize
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicLong
import javax.naming.SizeLimitExceededException


@Service
class UploadService {

    fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }


    fun uploadfile (filePartMono : Flux<FilePart>, contentLength: Long, id: String?): Mono<ResponseEntity<String>> {
        val basePath = Paths.get("./src/main/resources/files")
        val count = AtomicLong()
        return when {
            (id != null && !Files.exists(basePath.resolve(id))) -> throw FileNotFoundException()
            else -> {
                val id = id?:getRandomString(10)
                filePartMono
                    .switchIfEmpty(Mono.error(FileNotFoundException()))
                    .flatMap { fp: FilePart ->
                        when {
                            (count.incrementAndGet() > 1) -> Flux.error(IndexOutOfBoundsException())
                            else -> Flux.just(fp)
                        }
                    }
                    .single()
                    .flatMap { fp: FilePart ->
                        when {
                            (fp.filename().isBlank()) -> Mono.error(FileNotSelectException())
                            (contentLength > DataSize.ofKilobytes(6000)
                                .toBytes()) -> Mono.error(SizeLimitExceededException())

                            fp.filename().substring(fp.filename().lastIndexOf('.') + 1) !in setOf(
                                "jpeg",
                                "pdf",
                                "jpg",
                                "doc",
                                "txt",
                                "png"
                            ) -> Mono.error<ResponseEntity<String>?>(
                                UnsupportedMediaTypeException("File format not supported")
                            )

                            else -> {
//                                print("${filePartMono.count()} 1")
                                var filepath  = basePath.resolve(id)
                                Files.createDirectories(filepath)
                                return@flatMap fp.transferTo(filepath.resolve(fp.filename())).then(
                                    Mono.just(
                                        ResponseEntity.ok(
                                            UriComponentsBuilder.fromPath("http://localhost:8080/download/").path(id + "/" + fp.filename()).toUriString()
                                        )
                                    )
                                )
                            }
                        }
                    }
            }
        }


    }
}
