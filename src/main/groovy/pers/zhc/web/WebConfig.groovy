package pers.zhc.web

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.HiddenHttpMethodFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * @author bczhc
 */
@Configuration
class WebConfig {
   /* @Bean
    HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new OrderedHiddenHttpMethodFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
            }
        };
    }

    @Bean
    FilterRegistrationBean registration(HiddenHttpMethodFilter filter) {
        def registration = new FilterRegistrationBean(filter)
        registration.setEnabled(false)
        return registration
    }*/
}
