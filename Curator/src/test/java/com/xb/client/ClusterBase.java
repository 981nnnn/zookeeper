package com.xb.client;

public class ClusterBase extends StandaloneBase {

    private final static String CLUSTER_CONNECT_STR = "192.168.56.101:2181,192.168.56.101:2182,192.168.56.101:2183,192.168.56.101:2184";


    private static final int CLUSTER_SESSION_TIMEOUT = 60 * 1000;


    @Override
    protected String getConnectStr() {
        return CLUSTER_CONNECT_STR;
    }

    @Override
    protected int getSessionTimeout() {
        return CLUSTER_SESSION_TIMEOUT;
    }
}
