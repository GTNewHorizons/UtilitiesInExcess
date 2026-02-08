package com.fouristhenumber.utilitiesinexcess.common.multipart;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;

public class MultipartHandler implements IPartFactory {

    @Override
    public TMultiPart createPart(String name, boolean client) {
        if (name.equals("utilitiesinexcess:frame")) {
            return new FramePart();
        }
        return null;
    }

    public static void init() {
        MultiPartRegistry.registerParts(new MultipartHandler(), new String[] { "utilitiesinexcess:frame" });
    }
}
