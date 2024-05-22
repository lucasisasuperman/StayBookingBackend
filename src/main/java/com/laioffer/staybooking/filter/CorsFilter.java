//跨域访问，因为前后端分开部署，前后端是不同的域名
//client前端发送请求 server后端接受请求
//前后端不在一起：proxy要写成后端的域名，postman是localhost8080
//前后端两个网址会有安全隐患
//需要有一定的保护措施只对特定的请求支持
//在response header里加特定的信息
//preflight机制，发送的是get
//在发送get之前先发送一个options请求，和get网址没区别，是个http method
//server端返回response header，通过http header判断是否支持跨域访问
//这些从response header返回的数据会被browser check，如果不通过就会block，后面不会执行
//origin判断支持来自哪些域名的请求，*代表所有前端网址都支持
//header判断支持哪些header比如content type、authorization，必须全符合才会成功
//method：get post delete options这几种
//options主要看发送请求会发生什么，通过了才会发请求，options由浏览器发送
//如果用postman：不会检查是不是跨域，postman没有检查机制，不会进行之前的流程
//vim dockerfile from。。。为运行环境
//workdir 根目录
//用docker启动程序时先割出来一个运行空间，这个程序的运行环境是隔离的，不用docker的话运行环境是在一起的
//add操作把当前虚拟机上的文件copy到当前的运行空间，add前后文件名不需要一样，add前是上传文件名，add后面那个文件名无所谓
//expose8080开放端口因为前端要发送request到这个端口
//cmd java加这个文件名说明启动这个文件，启动的是add后面那个文件的名字，cmd java和add后面那个文件名字需要保持一直
//runtime = custom用dockerfile里面的
//instance class：F4运行的服务器配置
//env：flex和autoscaling配合，根据流量大小决定用的服务器个数min max
//network决定用的网络环境，default公共网络环境
//subnetwork第一次服务的名字default
package com.laioffer.staybooking.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component //spring component,为了decouple，程序在启动的时候会被创建一个bean
@Order(Ordered.HIGHEST_PRECEDENCE)//如果有很多filter。ordered决定这个filter被执行的顺序，数字越小先执行
public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //有请求进来，filter依次执行，如果都通过了，request最后call controller，filter chain代表当前的请求通过了，要不要继续给下一个header
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");//任意网址都可以访问后端api
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}