package com.pcl_solutions.maven.wagons.rsync_maven_wagon;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.events.SessionListener;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.proxy.ProxyInfoProvider;
import org.apache.maven.wagon.repository.Repository;

public class RsyncWagon implements Wagon {
    private final List<SessionListener> sessionListeners;
    private final List<TransferListener> transferListeners;
    private Repository repository;
    private AuthenticationInfo authenticationInfo;
    private ProxyInfo proxyInfo;
    private ProxyInfoProvider proxyInfoProvider;
    private String protocol;
    private String host;
    private String path;

    public RsyncWagon() {
        sessionListeners = new Vector<SessionListener>();
        transferListeners = new Vector<TransferListener>();
    }

    public void addSessionListener(final SessionListener listener) {
        sessionListeners.add(listener);
    }

    public void addTransferListener(final TransferListener listener) {
        transferListeners.add(listener);
    }

    public void connect(final Repository repository) {
        setRepository(repository);
    }

    public void connect(final Repository repository, final ProxyInfo proxyInfo) {
        setRepository(repository);
    }

    public void connect(final Repository repository, final ProxyInfoProvider proxyInfoProvider) {
        setRepository(repository);
    }

    public void connect(final Repository repository, final AuthenticationInfo authenticationInfo) {
        setRepository(repository);
    }

    public void connect(final Repository repository, final AuthenticationInfo authenticationInfo,
            final ProxyInfo proxyInfo) {
        setRepository(repository);
    }

    public void connect(final Repository repository, final AuthenticationInfo authenticationInfo,
            final ProxyInfoProvider proxyInfoProvider) {
        setRepository(repository);
    }

    public void disconnect() {
    }

    public void get(final String resourceName, final File destination) {
        try {
            runRsync(host + ":" + path + File.separator + resourceName, destination.getCanonicalPath(),
                    Arrays.asList("-aR", "--dirs", "--progress"));
        } catch (final Exception e) {
            System.out.println(e);
        }
    }

    public List<String> getFileList(final String destinationDirectory) {
        return new Vector<String>();
    }

    public boolean getIfNewer(final String resourceName, final File destination, final long timestamp) {
        try {
            System.out.println("getIfNewer " + resourceName + " to " + destination.getCanonicalPath());
        } catch (final Exception e) {
            System.out.println(e);
        }
        return false;
    }

    public int getReadTimeout() {
        return 0;
    }

    public Repository getRepository() {
        return null;
    }

    public int getTimeout() {
        return 0;
    }

    public boolean hasSessionListener(final SessionListener listener) {
        return sessionListeners.contains(listener);
    }

    public boolean hasTransferListener(final TransferListener listener) {
        return transferListeners.contains(listener);
    }

    public boolean isInteractive() {
        return false;
    }

    public void openConnection() {
    }

    public void put(final File source, final String destination) {
        try {
            runRsync(source.getCanonicalPath(), host + ":" + path + File.separator + destination,
                    Arrays.asList("-aR", "--dirs", "--progress"));
        } catch (final Exception e) {
            System.out.println(e);
        }
    }

    public void putDirectory(final File sourceDirectory, final String destinationDirectory) {
        try {
            final File remotePath = new File("target/rsync/" + path);
            remotePath.mkdirs();
            runRsync("target/rsync/", host + ":/", Arrays.asList("-rd", "--progress"));
            runRsync(sourceDirectory.getCanonicalPath() + "/", host + ":" + path + File.separator
                    + destinationDirectory, Arrays.asList("-avc", "--progress"));
        } catch (final Exception e) {
            System.out.println(e);
        }
    }

    public void removeSessionListener(final SessionListener listener) {
    }

    public void removeTransferListener(final TransferListener listener) {
    }

    public boolean resourceExists(final String resourceName) {
        return true;
    }

    public void setInteractive(final boolean interactive) {
    }

    public void setReadTimeout(final int timeoutValue) {
    }

    public void setTimeout(final int timeoutValue) {
    }

    public boolean supportsDirectoryCopy() {
        return true;
    }

    private void setRepository(final Repository repository) {
        this.repository = repository;

        final String url = repository.getUrl();
        final Pattern p = Pattern.compile("([^:]*):(([^/:]*):|)(.*)");
        final Matcher m = p.matcher(url);
        if (!m.matches()) {
            System.err.println("Error: Couldn't decode url '" + url + "'");
        } else {
            protocol = m.group(1);
            host = m.group(3);
            path = m.group(4);
            System.out.println("Decoded url '" + url + "' to:");
            System.out.println(" Protocol '" + protocol + "'");
            System.out.println(" Host '" + host + "'");
            System.out.println(" Path '" + path + "'");
        }
    }

    private void runRsync(final String source, final String destination, final List<String> options) throws Exception {
        final ArrayList<String> argv = new ArrayList<String>(options);
        String line;
        Process process;
        BufferedReader processOutput, processErrors;
        argv.add(0, "rsync");
        argv.add(source);
        argv.add("maven@" + destination);

        System.out.println("dry: " + argv);
        if (true) {
            process = Runtime.getRuntime().exec(argv.toArray(new String[0]));
            processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            processErrors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            do {
                line = processOutput.readLine();
                if (line != null) {
                    System.out.println(line);
                }
            } while (line != null);
            do {
                line = processErrors.readLine();
                if (line != null) {
                    System.out.println(line);
                }
            } while (line != null);
            process.waitFor();
        }
    }
}
