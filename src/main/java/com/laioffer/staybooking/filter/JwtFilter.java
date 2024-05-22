package com.laioffer.staybooking.filter;
import com.laioffer.staybooking.model.Authority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import com.laioffer.staybooking.repository.AuthorityRepository;
import com.laioffer.staybooking.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//json web token
//验证请求里有没有token，每个请求都要经过这个jwt filter验证是否有username
//如果token不正常，会发现没有权限，返回错误，在security config里配置了返回错误
//登录注册没token不会返回错误
//security config中定义了jwt filter会在authentication filter之前执行
@Component
public class JwtFilter extends OncePerRequestFilter { //request进来的时候先执行，先于所有的servlet
    private final String HEADER = "Authorization"; //filter里检查有没有authorization header
    private final String PREFIX = "Bearer ";
    private AuthorityRepository authorityRepository;
    private JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(AuthorityRepository authorityRepository, JwtUtil jwtUtil) {
        this.authorityRepository = authorityRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = httpServletRequest.getHeader(HEADER); //检查http header

        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(PREFIX)) { //如果有，根据token，取出来，没有的话就去下一个
            jwt = authorizationHeader.substring(PREFIX.length());
        }

        if (jwt != null && jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {//判断token是不是有效以及是否需要继续验证用securityContextHolder
            //只需要验证一次，存在线程里面
            String username = jwtUtil.extractUsername(jwt); //找username
            Authority authority = authorityRepository.findById(username).orElse(null);
            if (authority != null) {
                List<GrantedAuthority> grantedAuthorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(authority.getAuthority())});
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, grantedAuthorities);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));//用在security框架里，保存用户的信息
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
//filter chain proxy代表所有的filter但是不一定都用
//验证请求是不是合法，token找到username，在数据库中验证权限
//filter在servlet之前，先拿到用户权限再dispatch request