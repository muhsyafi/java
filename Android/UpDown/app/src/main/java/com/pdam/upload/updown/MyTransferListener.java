package com.pdam.upload.updown;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * Created by muhsyafi on 12/13/15.
 */
public class MyTransferListener implements FTPDataTransferListener {

    public void started() {

    }

    public void transferred(int length) {
        System.out.println(" transferred ..." + length);
    }

    public void completed() {

    }

    public void aborted() {

    }

    public void failed() {


    }

}