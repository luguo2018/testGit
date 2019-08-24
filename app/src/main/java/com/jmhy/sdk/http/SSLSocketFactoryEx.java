package com.jmhy.sdk.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class SSLSocketFactoryEx extends SSLSocketFactory {

	SSLContext sslContext = SSLContext.getInstance("TLS");

    public SSLSocketFactoryEx(KeyStore truststore) throws
            NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {

        super(truststore);


        sslContext.init(null, new TrustManager[] { new EasyX509TrustManager(null) }, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
            boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host,
                port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    private SSLContext getSSLContext() throws IOException {  
        return this.sslContext;  
    }  

    @Override
    public Socket connectSocket(Socket sock, String host, int port,  
            InetAddress localAddress, int localPort, HttpParams params)  
            throws IOException, UnknownHostException, ConnectTimeoutException {  
        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);  
        int soTimeout = HttpConnectionParams.getSoTimeout(params);  

        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);  
        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());  

        if ((localAddress != null) || (localPort > 0)) {  
            // we need to bind explicitly  
            if (localPort < 0) {  
                localPort = 0; // indicates "any"  
            }  
            InetSocketAddress isa = new InetSocketAddress(localAddress,  
                    localPort);  
            sslsock.bind(isa);  
        }  

        sslsock.connect(remoteAddress, connTimeout);  
        sslsock.setSoTimeout(soTimeout);  
        return sslsock;  

    }  

    public boolean isSecure(Socket socket) throws IllegalArgumentException {  
        return true;  
    }  

    // -------------------------------------------------------------------  
    // javadoc in org.apache.http.conn.scheme.SocketFactory says :  
    // Both Object.equals() and Object.hashCode() must be overridden  
    // for the correct operation of some connection managers  
    // -------------------------------------------------------------------  

    public boolean equals(Object obj) {  
        return ((obj != null) && obj.getClass().equals(  
                SSLSocketFactoryEx.class));  
    }  

    public int hashCode() {  
        return SSLSocketFactoryEx.class.hashCode();  
    }  
	
	
	
	

    
}

