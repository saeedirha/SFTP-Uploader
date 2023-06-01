package net.ghiassy.sftp_uploader.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ServerModel implements Serializable {

    private int port;
    private String host;
    private String username;
    private String password;
    private String protocol;
    private boolean Enabled;
    private boolean isTested;


    public ServerModel(String host, int port, String username, String password, String protocol) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.protocol = protocol;
        this.Enabled = true;
        this.isTested = false;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public boolean isTested() {
        return isTested;
    }

    public void setTested(boolean tested) {
        isTested = tested;
    }

    @NonNull
    @Override
    public String toString() {
        return "ServerModel{" +
                "port=" + port +
                ", host='" + host + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", protocol='" + protocol + '\'' +
                ", Enabled=" + Enabled +
                ", isTested=" + isTested;
    }
}
