package com.reactive.uploadFiles.controllers

import com.reactive.uploadFiles.exceptions.FileNotSelectException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.UnsupportedMediaTypeException
import java.io.FileNotFoundException
import javax.naming.SizeLimitExceededException

@ControllerAdvice
class ExceptionController {
    @ExceptionHandler(FileNotFoundException::class)
    fun notFound(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File Not Found Re-Check the Id or File Name"))
    }

    @ExceptionHandler(FileNotSelectException::class)
    fun notSelected():ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response("File Not Selected"))
    }

    @ExceptionHandler(IndexOutOfBoundsException::class)
    fun multipleFileSelected():ResponseEntity<Response>{
        return ResponseEntity.badRequest().body(Response("Mulitple File Selected"))
    }


    @ExceptionHandler(SizeLimitExceededException::class)
    fun largeFile(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File size too large"))
    }

    @ExceptionHandler(UnsupportedMediaTypeException::class)
    fun format(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("File format not supported"))
    }
    @ExceptionHandler(Exception::class)
    fun otherExceptions(): ResponseEntity<Response> {
        return ResponseEntity.badRequest().body(Response("Data is not present inside the Body"))
    }
    data class Response(var message:String?)
}