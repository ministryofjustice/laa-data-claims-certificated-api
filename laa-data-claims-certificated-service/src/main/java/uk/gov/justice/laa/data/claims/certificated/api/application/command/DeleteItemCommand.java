package uk.gov.justice.laa.data.claims.certificated.api.application.command;

/** Immutable command to delete an existing item. */
public record DeleteItemCommand(Long id) {}
