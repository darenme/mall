package com.darenme.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by darenme
 * date: 2018/8/12
 * time: 11:22
 */

public interface IFileService {
    String upload(MultipartFile file, String path);
}
