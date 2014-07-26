package org.litesoft.jetty;

import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.*;

import java.sql.*;
import java.util.*;

public final class EmbeddedJetty {
    private Server[] mServers;

    public EmbeddedJetty( PortServlets pPortServlet0, PortServlets... pPortServlets ) {
        List<Server> zServers = new ArrayList<>();
        zServers.add( createServer( pPortServlet0 ) );

        for ( PortServlets zPortServlet : pPortServlets ) {
            zServers.add( createServer( zPortServlet ) );
        }

        mServers = zServers.toArray( new Server[zServers.size()] );
    }

    private Server createServer( PortServlets pPortServlet ) {
        ServletHandler zServletHandler = new ServletHandler();
        for ( ServletSpec zServletSpec : pPortServlet.getServletSpecs() ) {
            zServletHandler.addServletWithMapping( new ServletHolder( zServletSpec.getServlet() ), zServletSpec.getPathSpec() );
        }

        ContextHandler zContextHandler = new ServletContextHandler();
        zContextHandler.setMimeTypes( getMimeTypes( pPortServlet.getAdditionalMimeTypes() ) );
        zContextHandler.setHandler( zServletHandler );

        Server zServer = new Server( pPortServlet.getPort() );
        zServer.setHandler( zContextHandler );

        zServer.setStopTimeout( 1000 );
        zServer.setStopAtShutdown( true );

        return zServer;
    }

    protected MimeTypes getMimeTypes( String... pAdditionalMimeTypes ) {
        // Handle files encoded in UTF-8
        MimeTypes zMimeTypes = new MimeTypes();
        if ( pAdditionalMimeTypes != null ) {
            for ( int i = 1; i < pAdditionalMimeTypes.length; i += 2 ) {
                String zExtension = pAdditionalMimeTypes[i - 1];
                String zType = pAdditionalMimeTypes[i];
                zMimeTypes.addMimeMapping( zExtension, zType );
            }
        }
        return zMimeTypes;
    }

    public int run()
            throws Exception {
        ServerKiller zKiller = new ServerKiller();
        Shutdown.Instance.set( zKiller );
        start();
        while ( !isRunning() ) {
            if ( isStopped() ) {
                throw new Error( "Stopped on start!" );
            }
            if ( isFailed() ) {
                throw new Error( "Failed on start!" );
            }
            Thread.sleep( 50 );
        }
        report( "running", "" );
        join();
        int zExitCode = zKiller.mExitCode;
        report( "exiting", " with: " + zExitCode );
        return zExitCode;
    }

    private void start()
            throws Exception {
        for ( Server zServer : mServers ) {
            zServer.start();
        }
    }

    private void stop()
            throws Exception {
        for ( Server zServer : mServers ) {
            zServer.stop();
        }
    }

    private void join()
            throws InterruptedException {
        for ( Server zServer : mServers ) {
            zServer.join();
        }
    }

    private boolean isFailed() {
        for ( Server zServer : mServers ) {
            if ( zServer.isFailed() ) {
                return true;
            }
        }
        return false;
    }

    private boolean isStopped() {
        for ( Server zServer : mServers ) {
            if ( zServer.isStopped() ) {
                return true;
            }
        }
        return false;
    }

    private boolean isRunning() {
        for ( Server zServer : mServers ) {
            if ( !zServer.isRunning() ) {
                return false;
            }
        }
        return true;
    }

    private void report( String pWhat, String pTail ) {
        System.out.println( "EmbeddedJetty Server " + pWhat + " @ '" + new Timestamp( System.currentTimeMillis() ) + "'" + pTail );
    }

    private class ServerKiller implements Shutdown {
        private volatile int mExitCode;

        @Override
        public void request( int pExitCode ) {
            if ( pExitCode != 0 ) {
                mExitCode = pExitCode;
            }
            new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep( 100 );
                        stop();
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
        }
    }
}
