package com.dementor.domain.screenShare.dto;

import java.util.List;

public record ScreenShareTokenResponse(
    String token,
    List<IceServer> iceServers,
    Long applyId
) {
    public record IceServer(
        String urls,
        String username,
        String credential
    ) {}
}


