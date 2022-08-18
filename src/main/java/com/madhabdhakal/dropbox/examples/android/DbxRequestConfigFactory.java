package com.madhabdhakal.dropbox.examples.android;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;

public class DbxRequestConfigFactory {
    private static DbxRequestConfig sDbxRequestConfig;

    public static DbxRequestConfig getRequestConfig() {
        if (sDbxRequestConfig == null) {
            sDbxRequestConfig = DbxRequestConfig.newBuilder("dropbox/WidelineDev")
                    .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build();
        }
        return sDbxRequestConfig;
    }
}
