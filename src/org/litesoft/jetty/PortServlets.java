package org.litesoft.jetty;

import javax.servlet.*;
import java.util.*;

public final class PortServlets {
    private final int mPort;
    private final List<ServletSpec> mServletSpecs = new ArrayList<>();
    private final String[] mAdditionalMimeTypes;

    public PortServlets( int pPort, Servlet pServlet, String pPathSpec, String... pAdditionalMimeTypes ) {
        mPort = pPort;
        mAdditionalMimeTypes = pAdditionalMimeTypes;
        addServlet( pServlet, pPathSpec );
    }

    public PortServlets addServlet( Servlet pServlet, String pPathSpec ) {
        mServletSpecs.add( new ServletSpec( pServlet, pPathSpec ) );
        return this;
    }

    public int getPort() {
        return mPort;
    }

    public List<ServletSpec> getServletSpecs() {
        return mServletSpecs;
    }

    public String[] getAdditionalMimeTypes() {
        return mAdditionalMimeTypes;
    }
}
