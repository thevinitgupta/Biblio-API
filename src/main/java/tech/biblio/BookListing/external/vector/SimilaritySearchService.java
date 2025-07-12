package tech.biblio.BookListing.external.vector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.biblio.BookListing.constants.ApplicationConstants;
import tech.biblio.BookListing.exceptions.SimilaritySearchException;
import tech.biblio.BookListing.external.dto.SearchResponseDTO;
import tech.biblio.BookListing.external.dto.SearchResponseDataDTO;
import tech.biblio.BookListing.utils.Helper;

import java.util.List;

@Service
@Slf4j
public class SimilaritySearchService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Helper helper;

    @Value("${RECOMMEND_API_URL}")
    private String recommendApiUrl;

    @Value("${RECOMMEND_API_KEY}")
    private String recommendApiKey;

    public List<SearchResponseDataDTO> fetchSimilarPostsById(String postId) {
        if(helper.isNullOrEmpty(recommendApiUrl)){
            log.error("Please defined recommend API URL to proceed");
        }

        if(helper.isNullOrEmpty(recommendApiKey)){
            log.error("Please defined recommend API Key to proceed");
        }


        String apiEndpoint = recommendApiUrl + "/vector/similar/"+postId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(ApplicationConstants.RECOMMEND_API_KEY_HEADER, recommendApiKey);
//        EnqueueRequestBody body = new EnqueueRequestBody(postId);

        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<SearchResponseDTO> searchResponseEntity = restTemplate.exchange(apiEndpoint,
                HttpMethod.GET,httpEntity, SearchResponseDTO.class);
        SearchResponseDTO searchResponse = searchResponseEntity.getBody();
        if(searchResponse.error()!=null){
            throw new SimilaritySearchException(searchResponse.error());
        }
        if(searchResponse.data().isEmpty()){
            throw new SimilaritySearchException("No Similar Posts Found");
        }
        return searchResponse.data();

    }

}
