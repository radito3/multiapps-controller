package com.sap.cloud.lm.sl.cf.process.util;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Named;

import com.sap.cloud.lm.sl.cf.core.util.FileUtils;
import com.sap.cloud.lm.sl.cf.process.Messages;
import com.sap.cloud.lm.sl.common.ContentException;
import com.sap.cloud.lm.sl.common.SLException;

@Named
public class ApplicationArchiveReader {

    private static final int BUFFER_SIZE = 4 * 1024; // 4KB

    public String calculateApplicationDigest(ApplicationArchiveContext applicationArchiveContext) {
        try {
            computeArchiveDigest(applicationArchiveContext);
            return applicationArchiveContext.getApplicationDigestCalculator()
                                            .getDigest();
        } catch (IOException e) {
            throw new SLException(e, Messages.ERROR_RETRIEVING_MTA_MODULE_CONTENT, applicationArchiveContext.getModuleFileName());
        }
    }

    private void computeArchiveDigest(ApplicationArchiveContext applicationArchiveContext) throws IOException {
        String moduleFileName = applicationArchiveContext.getModuleFileName();
        ZipEntry zipEntry = getFirstZipEntry(applicationArchiveContext);
        long maxSizeInBytes = applicationArchiveContext.getMaxSizeInBytes();
        do {
            if (!zipEntry.isDirectory()) {
                if (zipEntry.getSize() > maxSizeInBytes) {
                    throw new ContentException(Messages.SIZE_OF_APP_EXCEEDS_MAX_SIZE_LIMIT, maxSizeInBytes);
                }
                calculateDigestFromArchive(applicationArchiveContext);
            }
        } while ((zipEntry = getNextEntryByName(moduleFileName, applicationArchiveContext)) != null);
    }

    public ZipEntry getFirstZipEntry(ApplicationArchiveContext applicationArchiveContext) throws IOException {
        String moduleFileName = applicationArchiveContext.getModuleFileName();
        ZipEntry zipEntry = getNextEntryByName(moduleFileName, applicationArchiveContext);
        if (zipEntry == null) {
            throw new ContentException(com.sap.cloud.lm.sl.mta.Messages.CANNOT_FIND_ARCHIVE_ENTRY, moduleFileName);
        }
        return zipEntry;
    }

    protected void calculateDigestFromArchive(ApplicationArchiveContext applicationArchiveContext) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        ZipInputStream zipInputStream = applicationArchiveContext.getZipInputStream();
        DigestCalculator applicationDigestCalculator = applicationArchiveContext.getApplicationDigestCalculator();
        while ((read = zipInputStream.read(buffer)) != -1) {
            applicationDigestCalculator.updateDigest(buffer, 0, read);
        }
    }

    public ZipEntry getNextEntryByName(String name, ApplicationArchiveContext applicationArchiveContext) throws IOException {
        ZipInputStream zipInputStream = applicationArchiveContext.getZipInputStream();
        for (ZipEntry zipEntry; (zipEntry = zipInputStream.getNextEntry()) != null;) {
            if (zipEntry.getName()
                        .startsWith(name)) {
                validateEntry(zipEntry);
                return zipEntry;
            }
        }
        return null;
    }

    protected void validateEntry(ZipEntry entry) {
        FileUtils.validatePath(entry.getName());
    }

}
