package com.haya.service;

import com.haya.repository.TokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/** Removes expired tokens so the token table does not grow without bound. */
@Component
@EnableScheduling
public class TokenCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupJob.class);

    private final TokenRepository tokens;

    public TokenCleanupJob(TokenRepository tokens) {
        this.tokens = tokens;
    }

    @Scheduled(cron = "0 15 3 * * *")
    @Transactional
    public void purgeExpiredTokens() {
        int removed = tokens.deleteExpired(Instant.now());
        if (removed > 0) {
            log.info("Purged {} expired tokens", removed);
        }
    }
}
