package uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.readmodel;

/** Read-side projection of an item — scalar fields only, no entity associations. */
public record ItemReadModel(Long id, String name, String description) {}
