package ua.kshanovskyi.controller;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ua.kshanovskyi.service.NasaService;

@RestController
@RequestMapping("/mars/pictures/largest")
@RequiredArgsConstructor
public class NasaController {

    private final NasaService nasaService;

    @GetMapping( produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getLargestPicture(@RequestParam int sol, @RequestParam(required = false) String camera) {
        return nasaService.getLargestUrl(sol, Optional.ofNullable(camera));
    }
}
