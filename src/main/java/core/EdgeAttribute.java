package core;

/**
 * This class defines a list of attributes that can be associated with edges.
 * These attributes can be used to modify cost calculation or filter certain
 * edges out of the graph.
 */
public enum EdgeAttribute {
    INDOORS,
    NOT_HANDICAP_ACCESSIBLE,
    EDGE_REMOVED,
    STAIRS,
    ELEVATOR,
}
