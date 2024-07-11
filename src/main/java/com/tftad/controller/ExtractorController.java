package com.tftad.controller;

import com.tftad.service.ExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExtractorController {

    private final ExtractorService extractorService;

}
