import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        String uri = httpRequest.getRequestURI();

        // Pages accessibles sans connexion
        if (uri.contains("/login") || uri.contains("/css/") || uri.endsWith(".css")) {
            chain.doFilter(request, response);
            return;
        }

        // Vérifier la session
        if (session != null && session.getAttribute("login") != null) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}