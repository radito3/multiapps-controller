package com.sap.cloud.lm.sl.cf.process.util;

import com.sap.cloud.lm.sl.common.util.DigestHelper;

import java.security.MessageDigest;

public class DigestCalculator {

    private final MessageDigest messageDigest;

    public DigestCalculator(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }

    public void updateDigest(byte[] bytes, int offset, int len) {
        messageDigest.update(bytes, offset, len);
    }

    public String getDigest() {
        return DigestHelper.digestToString(messageDigest.digest())
                           .toUpperCase();
    }
}
