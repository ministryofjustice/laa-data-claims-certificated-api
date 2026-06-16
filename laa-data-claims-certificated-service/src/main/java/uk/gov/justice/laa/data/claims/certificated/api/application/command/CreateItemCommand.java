package uk.gov.justice.laa.data.claims.certificated.api.application.command;

/** Immutable command to create a new item. */
public record CreateItemCommand(String name, String description) {}
