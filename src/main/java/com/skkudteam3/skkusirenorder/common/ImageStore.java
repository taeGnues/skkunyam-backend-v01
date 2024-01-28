package com.skkudteam3.skkusirenorder.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ImageStore {

    public String createStoreFileName(String originalFilename) {
        // 서버에 저장하는 파일명
        String uuid = UUID.randomUUID().toString(); // uuid만 하면 qwe-qwe-qwe-123 같이 생성됨.
        String ext = extractExt(originalFilename); // 확장자명 추출

        return uuid + "." + ext;
    }

    public String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos+1); // 확장자명
    }
}
