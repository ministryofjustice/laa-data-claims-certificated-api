package uk.gov.justice.laa.data.claims.certificated.api.application.command.createitem;

/** Immutable command to create a new item. */
public record CreateItemCommand(String name, String description) {}
