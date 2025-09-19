package com.example.pagination.paging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class CursorCodec {

  private final ObjectMapper mapper;

  public CursorCodec(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public String encode(CursorPayload p) {
    try { return Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(p)); }
    catch (Exception e) { throw new IllegalArgumentException("encode failed", e); }
  }

  public CursorPayload decode(String token) {
    if (token == null || token.isBlank()) return null;
    try { return mapper.readValue(Base64.getUrlDecoder().decode(token), CursorPayload.class); }
    catch (Exception e) { throw new IllegalArgumentException("invalid cursor", e); }
  }

  public static ScrollPosition toPosition(CursorPayload payload, boolean backward) {
    if (payload == null || payload.keys() == null) return ScrollPosition.keyset();
    return backward ? ScrollPosition.backward(payload.keys()) : ScrollPosition.forward(payload.keys());
  }
}
