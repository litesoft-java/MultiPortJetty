package org.litesoft.jetty;

import javax.servlet.*;

public final class ServletSpec {
    private final Servlet mServlet;
    private final String mPathSpec;

    public ServletSpec( Servlet pServlet, String pPathSpec ) {
        mServlet = pServlet;
        mPathSpec = pPathSpec;
    }

    public Servlet getServlet() {
        return mServlet;
    }

    public String getPathSpec() {
        return mPathSpec;
    }
}
