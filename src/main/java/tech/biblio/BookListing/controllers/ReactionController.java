package tech.biblio.BookListing.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.dto.ReactionRequestDTO;
import tech.biblio.BookListing.dto.ReactionsDTO;
import tech.biblio.BookListing.dto.ReactionsResponseDTO;
import tech.biblio.BookListing.dto.ResponseDTO;
import tech.biblio.BookListing.entities.EntityType;
import tech.biblio.BookListing.services.ReactionService;
import tech.biblio.BookListing.utils.Helper;

@RestController
@RequestMapping("reaction")
@Slf4j
public class ReactionController {

    @Autowired
    ReactionService reactionService;

    @Autowired
    Helper helper;

    @GetMapping
    public ResponseEntity<?> getReactionsForEntity(@RequestParam(name = "type") EntityType entityType, @RequestParam(name = "id") String entityId) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        if(helper.isNullOrEmpty(username)){
            username = "";
        }

        if(helper.isNullOrEmpty(entityId)){
            throw new BadRequestException("Reactions can be fetched for specific post/comment only");
        }

        System.out.println("Entity Type :"+entityType);

        ReactionsDTO reactionsDTO = reactionService
                .getReactions(entityType, entityId, username);
        ReactionsResponseDTO.ReactionsResponseDTOBuilder reactResDTOBuilder =
                ReactionsResponseDTO.builder();
        if(reactionsDTO.userReactions().isEmpty()){
            reactResDTOBuilder.message("No reactions found for user");
        }
        else {
            reactResDTOBuilder.message("Success");
        }
        reactResDTOBuilder.reactions(reactionsDTO);
        return new ResponseEntity<>(reactResDTOBuilder.build(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postReaction(@RequestBody ReactionRequestDTO reactionRequestDTO) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        if(helper.isNullOrEmpty(reactionRequestDTO.entityId())){
            throw new BadRequestException("Entity ID is empty");
        }

        reactionService.toggleReaction(reactionRequestDTO, username);
        return new ResponseEntity<>(new ResponseDTO(
                HttpStatus.OK.getReasonPhrase(),
                "Reaction updated successfully"),
                HttpStatus.OK);
    }
}
