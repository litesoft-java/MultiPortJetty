package org.litesoft.jetty;

public interface Shutdown {
    void request( int pExitCode );

    static class Instance {
        private static Shutdown SHUTDOWN;

        public static synchronized Shutdown get() {
            Shutdown zInstance = SHUTDOWN;
            if ( zInstance != null ) {
                return zInstance;
            }
            throw new IllegalStateException( "Shutdown.Instance not initialized YET!" );
        }

        public static synchronized void set( Shutdown pShutdown ) {
            if ( pShutdown == null ) {
                throw new IllegalArgumentException( "Shutdown.Instance may NOT be set to null!" );
            }
            SHUTDOWN = pShutdown;
        }
    }
}
