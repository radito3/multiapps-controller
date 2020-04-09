package com.sap.cloud.lm.sl.cf.process.util;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipInputStream;

import com.sap.cloud.lm.sl.cf.persistence.services.FileUploader;

public class ApplicationArchiveContext {

    private final ZipInputStream zipInputStream;
    private final String moduleFileName;
    private final long maxSizeInBytes;
    private DigestCalculator applicationDigestCalculator;

    public ApplicationArchiveContext(InputStream inputStream, String moduleFileName, long maxSizeInBytes) {
        this.zipInputStream = new ZipInputStream(inputStream);
        this.moduleFileName = moduleFileName;
        this.maxSizeInBytes = maxSizeInBytes;
        createDigestCalculator();
    }

    private void createDigestCalculator() {
        try {
            this.applicationDigestCalculator = new DigestCalculator(MessageDigest.getInstance(FileUploader.DIGEST_METHOD));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public ZipInputStream getZipInputStream() {
        return zipInputStream;
    }

    public String getModuleFileName() {
        return moduleFileName;
    }

    public long getMaxSizeInBytes() {
        return maxSizeInBytes;
    }

    public DigestCalculator getApplicationDigestCalculator() {
        return applicationDigestCalculator;
    }

}
