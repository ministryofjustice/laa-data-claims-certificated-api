package uk.gov.justice.laa.data.claims.certificated.api.utils;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class Uuid7Generator {
  public UUID generate() {
    // Implementation for generating UUIDv7 goes here
    // This currently returns UUIDv4 as a placeholder
    return UUID.randomUUID(); // Placeholder implementation
  }
}
