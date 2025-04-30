package com.miniproj.util;

import java.util.HashMap;
import java.util.Map;

public class ImageMimeType {

    private static Map<String, String> imageMimeType;

    {

    }

    static {
        // static 멤버를 초기화하는 블럭

        imageMimeType = new HashMap<String, String>();

        imageMimeType.put("jpg", "image/jpeg");
        imageMimeType.put("jpeg", "image/jpeg");
        imageMimeType.put("gif", "image/gif");
        imageMimeType.put("png", "image/png");
    }

    public static boolean isImage(String ext){
        return imageMimeType.containsKey(ext);
    }
}
