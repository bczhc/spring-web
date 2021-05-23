package pers.zhc.web.controller.apps.sometools


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

/**
 * @author bczhc
 */
@RestController
class RegisterDiary {
    @Autowired
    private HttpServletRequest request

    @RequestMapping("/some-tools-app/register-diary")
    def register(@RequestParam("username") String username) {

    }
}
