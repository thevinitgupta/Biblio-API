package tech.biblio.BookListing.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthorizationEvents {
    @EventListener
    public void onFailure(AuthorizationDeniedEvent authorizationDeniedEvent) {
        log.error("User Authorization failed for user : {} due to : {}",
                authorizationDeniedEvent.getAuthentication().get().getName(),
                authorizationDeniedEvent.getAuthorizationDecision().toString());
    }
}
