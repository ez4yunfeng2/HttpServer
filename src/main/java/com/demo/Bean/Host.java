package com.demo.Bean;

import java.nio.file.Path;

/**
 * 主机Bean文件
 */
public class Host {
    private String hostName;
    private String topDomainName;
    private Path   dirPath;
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getTopDomainName() {
        return topDomainName;
    }

    public void setTopDomainName(String topDomainName) {
        this.topDomainName = topDomainName;
    }

    public Path getDirPath() {
        return dirPath;
    }

    public void setDirPath(Path dirPath) {
        this.dirPath = dirPath;
    }

    public Host(){

    }
    public Host(String line){
        int index  = line.lastIndexOf(".");
        if (index < 0){
            hostName = "www";
            topDomainName = line;
        }else {
            hostName = line.substring(0,index);
            topDomainName = line.substring(index+1);
        }
    }

    @Override
    public String toString() {
        return "Host{" +
                "hostName='" + hostName + '\'' +
                ", topDomainName='" + topDomainName + '\'' +
                ", dirPath=" + dirPath +
                '}';
    }
    @Override
    public boolean equals(Object host){
        Host h = (Host) host;
        return h.getHostName().equals(getHostName()) && h.getTopDomainName().equals(getTopDomainName());
    }
}
