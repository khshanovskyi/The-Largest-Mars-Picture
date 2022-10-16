package ua.kshanovskyi.service;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import ua.kshanovskyi.entity.Photo;
import ua.kshanovskyi.entity.Photos;

@Service
@RequiredArgsConstructor
public class NasaService {

    private final RestTemplate restTemplate;

    @Value("${nasa.url}")
    private String url;

    @Value("${nasa.key}")
    private String key;

    @Cacheable("largest-nasa-picture")
    public byte [] getLargestUrl(int sol, Optional<String> camera){
        return getImgUrls(sol, camera)
          .parallelStream()
          .map(this::extractAndSetPhotoSize)
          .max(Comparator.comparing(Photo::getSize))
          .map(photo -> restTemplate.getForObject(photo.getUrl(), byte[].class))
          .orElseThrow(() -> new  IllegalStateException("Something went wrong in extraction the largest NASA picture"));
    }

    private Photo extractAndSetPhotoSize(Photo photo){
        photo.setSize(restTemplate.headForHeaders(photo.getUrl()).getContentLength());
        return photo;
    }

    private List<Photo> getImgUrls(int sol, Optional<String> camera){
        return Objects.requireNonNull(restTemplate.getForObject(generateUri(sol, camera), Photos.class))
          .photos();
    }

    private String generateUri(int sol, Optional<String> camera){
        return UriComponentsBuilder.fromHttpUrl(url)
          .queryParam("api_key", key)
          .queryParam("sol", sol)
          .queryParamIfPresent("camera", camera)
          .build()
          .toUriString();
    }

}
