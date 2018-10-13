package me.hdpe.pushfight.server.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class IndexController {

    @RequestMapping(value = ["", "/"], produces = ["text/html"])
    fun index(): String = "redirect:/docs/"
}