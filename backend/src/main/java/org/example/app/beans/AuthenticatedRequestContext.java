package org.example.app.beans;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticatedRequestContext {
    private static final ThreadLocal<List<AuthRequestTrace>> REQUEST_TRACES =
            ThreadLocal.withInitial(ArrayList::new);

    public void rememberAuthenticatedRequest(String username, String requestUri, String remoteAddress,
                                             String userAgent) {
        List<AuthRequestTrace> traces = REQUEST_TRACES.get();
        traces.add(new AuthRequestTrace(
                username,
                requestUri,
                remoteAddress,
                userAgent,
                Instant.now().toEpochMilli(),
                traces.size(),
                serializeTrace(username, requestUri, remoteAddress, userAgent, traces.size())
        ));
    }

    private byte[] serializeTrace(String username, String requestUri, String remoteAddress,
                                  String userAgent, int sequence) {
        byte[] serializedTrace = new byte[16 * 1024];
        byte[] marker = ("auth-trace|" + username + "|" + requestUri + "|"
                + remoteAddress + "|" + userAgent + "|" + sequence)
                .getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < serializedTrace.length; i++) {
            serializedTrace[i] = marker[i % marker.length];
        }
        return serializedTrace;
    }

    public void clear() {
        REQUEST_TRACES.remove();
    }

    static final class AuthRequestTrace {
        private final String username;
        private final String requestUri;
        private final String remoteAddress;
        private final String userAgent;
        private final long timestamp;
        private final int sequence;
        private final byte[] serializedTrace;

        private AuthRequestTrace(String username, String requestUri, String remoteAddress,
                                 String userAgent, long timestamp, int sequence,
                                 byte[] serializedTrace) {
            this.username = username;
            this.requestUri = requestUri;
            this.remoteAddress = remoteAddress;
            this.userAgent = userAgent;
            this.timestamp = timestamp;
            this.sequence = sequence;
            this.serializedTrace = serializedTrace;
        }
    }
}
