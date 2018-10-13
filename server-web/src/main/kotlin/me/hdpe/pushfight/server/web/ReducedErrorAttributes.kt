package me.hdpe.pushfight.server.web

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest

class ReducedErrorAttributes : DefaultErrorAttributes(false) {

    override fun getErrorAttributes(webRequest: WebRequest, includeStackTrace: Boolean): MutableMap<String, Any?> {
        val attrs = super.getErrorAttributes(webRequest, includeStackTrace)

        val bindingResult = extractBindingResult(webRequest)
        if (bindingResult != null) {
            attrs["details"] = bindingResult.allErrors.map { it.defaultMessage }
            attrs -= arrayOf("errors", "message")
        }

        return attrs
    }

    private fun extractBindingResult(webRequest: WebRequest): BindingResult? {
        val error = getError(webRequest)
        return when (error) {
            is MethodArgumentNotValidException -> error.bindingResult
            is BindException -> error.bindingResult
            else -> null
        }
    }
}