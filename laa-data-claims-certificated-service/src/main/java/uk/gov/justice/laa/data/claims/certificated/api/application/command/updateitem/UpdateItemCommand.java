package uk.gov.justice.laa.data.claims.certificated.api.application.command.updateitem;

/** Immutable command to update an existing item. */
public record UpdateItemCommand(Long id, String name, String description) {}
