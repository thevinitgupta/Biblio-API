package tech.biblio.BookListing.external.queue;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tech.biblio.BookListing.constants.ApplicationConstants;
import tech.biblio.BookListing.external.dto.EnqueueRequestBody;
import tech.biblio.BookListing.external.dto.EnqueueResponseDTO;
import tech.biblio.BookListing.utils.Helper;

import java.util.List;

@Service
@Slf4j
public class PostVectorQueue {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Helper helper;

    @Value("${RECOMMEND_API_URL}")
    private String recommendApiUrl;

    @Value("${RECOMMEND_API_KEY}")
    private String recommendApiKey;


    @Async("postQueueExecutor")
    public void addPostIdToVectorQueue(String postId){
        if(helper.isNullOrEmpty(recommendApiUrl)){
            log.error("Please defined recommend API URL to proceed");
        }

        if(helper.isNullOrEmpty(recommendApiKey)){
            log.error("Please defined recommend API Key to proceed");
        }

        if(helper.isNullOrEmpty(postId)){
            log.error("Post ID for enqueue NOT FOUND!");
        }

        try{

            String apiEndpoint = recommendApiUrl+"/queue/add";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ApplicationConstants.RECOMMEND_API_KEY_HEADER, recommendApiKey);

        EnqueueRequestBody enqueueRequestBody = new EnqueueRequestBody(postId);
        HttpEntity<?> httpEntity = new HttpEntity<>(enqueueRequestBody, headers);
        ResponseEntity<EnqueueResponseDTO> enqueueResponse = restTemplate
                .exchange(apiEndpoint, HttpMethod.POST, httpEntity, EnqueueResponseDTO.class);
        EnqueueResponseDTO responseDTO = enqueueResponse.getBody();
            assert responseDTO != null;
            log.info("Enqueue operation successfull for PostID={}\nResponse from Recommend API: {}", postId, responseDTO.message());
        } catch (RestClientException e) {
            log.error("Error calling enqueue operation for Post : {} ------ {}", postId , e.getLocalizedMessage());
        }
    }
}
