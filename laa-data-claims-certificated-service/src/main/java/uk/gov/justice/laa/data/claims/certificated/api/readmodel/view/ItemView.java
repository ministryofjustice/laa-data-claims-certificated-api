package uk.gov.justice.laa.data.claims.certificated.api.readmodel.view;

/** Read-side projection of an item — scalar fields only, no entity associations. */
public record ItemView(Long id, String name, String description) {}
