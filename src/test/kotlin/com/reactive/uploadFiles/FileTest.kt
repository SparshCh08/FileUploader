package com.reactive.uploadFiles

import com.reactive.uploadFiles.Services.DownloadService
import com.reactive.uploadFiles.Services.UploadService
import com.reactive.uploadFiles.controllers.DownloadController
import com.reactive.uploadFiles.exceptions.FileNotSelectException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.util.unit.DataSize
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.FileNotFoundException
import java.nio.file.Path
import javax.naming.SizeLimitExceededException

@ExtendWith(MockitoExtension::class)
class FileTest {

    @InjectMocks
    lateinit var downloadController: DownloadController
    @InjectMocks
    private lateinit var uploadService: UploadService
    @InjectMocks
    private lateinit var downloadService: DownloadService
    @Test
    fun uploadTest(){
        val fp: FilePart = mock(FilePart::class.java)
        val id: String = "stxobZ2Dsg"
        Mockito.`when`(fp.filename()).thenReturn("abc.txt")
        Mockito.`when`(fp.transferTo(Mockito.any(Path::class.java)))
            .thenReturn(Mono.empty())

        val response: Mono<ResponseEntity<String>> = uploadService.uploadfile(Flux.just(fp), DataSize.ofKilobytes(500).toBytes(), id)

        StepVerifier.create(response).expectNextMatches{ ob -> ob.statusCode.is2xxSuccessful}
            .verifyComplete()

    }

    @Test
    fun fileLimitExceedTest(){
        val fp: FilePart = mock(FilePart::class.java)
        val id: String = "stxobZ2Dsg"
        Mockito.`when`(fp.filename()).thenReturn("abc.pdf")
        var response: Mono<ResponseEntity<String>> = uploadService.uploadfile(Flux.just(fp), DataSize.ofKilobytes(50000).toBytes(), id)

        StepVerifier.create(response)
            .verifyError(SizeLimitExceededException::class.java)
    }


    @Test
    fun fileNotSelectedTest(){
        val fp: FilePart = mock(FilePart::class.java)
        val id: String = "stxobZ2Dsg"
        Mockito.`when`(fp.filename()).thenReturn("")
        var response: Mono<ResponseEntity<String>> = uploadService.uploadfile(Flux.just(fp),0L, id)

        StepVerifier.create(response).verifyError(FileNotSelectException::class.java)
    }

    @Test
    fun fileFormatTest(){
        val fp: FilePart = mock(FilePart::class.java)
        val id: String = "stxobZ2Dsg"
        Mockito.`when`(fp.filename()).thenReturn("abc.csv")

        var response: Mono<ResponseEntity<String>> = uploadService.uploadfile(Flux.just(fp), DataSize.ofKilobytes(500).toBytes(), id)

        StepVerifier.create(response)
            .verifyError(UnsupportedMediaTypeException::class.java)
    }

    @Test
    fun downloadTest(){
//        Mockito.`when`(Files.exists(Mockito.any(Path::class.java))).thenReturn(true)
        val id: String = "stxobZ2Dsg"
        val fp:FilePart= mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("just work.jpg")


        var response:Mono<ResponseEntity<Resource>> = downloadService.downloadFile(id, fp.filename())

        StepVerifier.create(response)
            .expectNextMatches{ob -> ob.statusCode.is2xxSuccessful}
            .verifyComplete()
    }

    @Test
    fun downloadTestError(){
        val id: String = "stxobZ2D"
        val fp: FilePart = mock(FilePart::class.java)
        Mockito.`when`(fp.filename()).thenReturn("Dogs.jpg")

        var response: Mono<ResponseEntity<Resource>> = downloadService.downloadFile(id, fp.filename())

        StepVerifier.create(response)
            .verifyError(FileNotFoundException::class.java)
    }

//        @Test
//    fun downloadControllerTest(){
//         val serviceForDownloadControllerTest: DownloadService = mock(DownloadService::class.java)
//        val id = "alskjf0139"
//        val fileName = "Hello.jpg"
//            val mockResource = mock(UrlResource::class.java)
//            val res:ResponseEntity<Resource> = ResponseEntity.ok().body(mockResource)
//        Mockito.`when`(serviceForDownloadControllerTest.downloadFile(id, fileName)).thenReturn(Mono.fromSupplier {res})
//        val response : Mono<ResponseEntity<Resource>> = downloadController.downloadFile(id, fileName)
//        StepVerifier.create(response).expectNextMatches{ ob -> ob.statusCode.is2xxSuccessful}
//            .verifyComplete()
//    }
}






