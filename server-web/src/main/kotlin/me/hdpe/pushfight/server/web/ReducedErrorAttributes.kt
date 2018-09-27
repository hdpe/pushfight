package me.hdpe.pushfight.server.web

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest

class ReducedErrorAttributes : DefaultErrorAttributes(false) {

    override fun getErrorAttributes(webRequest: WebRequest, includeStackTrace: Boolean): MutableMap<String, Any?> {
        val attrs = super.getErrorAttributes(webRequest, includeStackTrace)

        val error = getError(webRequest)
        if (error is MethodArgumentNotValidException) {
            attrs["errors"] = error.bindingResult.allErrors.map { it.toString() }
        }

        return attrs
    }
}