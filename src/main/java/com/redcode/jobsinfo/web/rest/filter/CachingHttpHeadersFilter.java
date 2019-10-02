package com.redcode.jobsinfo.web.rest.filter;


import com.redcode.jobsinfo.config.RedcodeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CachingHttpHeadersFilter implements Filter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final String[] corsAllowed;
    private final String corsAllowedHeader;
    private final String corsAllowedMethods;

    public static final int DEFAULT_DAYS_TO_LIVE = 1461; // 4 years
    public static final long DEFAULT_SECONDS_TO_LIVE = TimeUnit.DAYS.toMillis(DEFAULT_DAYS_TO_LIVE);

    // We consider the last modified date is the start up time of the server
    public final static long LAST_MODIFIED = System.currentTimeMillis();

    private long cacheTimeToLive = DEFAULT_SECONDS_TO_LIVE;

    private RedcodeProperties redcodeProperties;

    @Autowired
    public CachingHttpHeadersFilter(RedcodeProperties redcodeProperties,
                                    @Value("${redcode.cors.allowed-origins}") String[] corsAllowed,
                                    @Value("${redcode.cors.allowed-headers}") String corsAllowedHeader,
                                    @Value("${redcode.cors.allowed-methods}") String corsAllowedMethods) {
        this.redcodeProperties = redcodeProperties;
        this.corsAllowed = corsAllowed;
        this.corsAllowedHeader = corsAllowedHeader;
        this.corsAllowedMethods = corsAllowedMethods;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String remoteOrigin = request.getHeader("origin");

        //log.debug(" from filter " + corsAllowed[0] + "$$" + corsAllowed[1]);
        if (Arrays.asList(corsAllowed).contains(remoteOrigin)) {
            response.setHeader("Access-Control-Allow-Origin", remoteOrigin);
        } else {
            response.setHeader("Access-Control-Allow-Origin", corsAllowed[0]);
        }
        response.setHeader("Access-Control-Allow-Methods", corsAllowedMethods);
        //response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", corsAllowedHeader);
        response.setHeader("Access-Control-Allow-Credentials", "true");


        //
        response.setHeader("Cache-Control", "max-age=" + cacheTimeToLive + ", public");
        response.setHeader("Pragma", "no-cache");

        // Setting Expires header, for proxy caching
        // response.setDateHeader("Expires", cacheTimeToLive + System.currentTimeMillis());
        response.setDateHeader("Expires", 0);

        // Setting the Last-Modified header, for browser caching
        response.setDateHeader("Last-Modified", LAST_MODIFIED);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}