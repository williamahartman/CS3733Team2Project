package core;

/**
 * This class defines a list of attributes that can be associated with edges.
 * These attributes can be used to modify cost calculation or filter certain
 * edges out of the graph.
 */
public enum EdgeAttribute {
    //should delete indoors and handicap_accessible
    INDOORS,
    OUTDOORS,
    NOT_HANDICAP_ACCESSIBLE,
    HANDICAP_ACCESSIBLE,
    EDGE_REMOVED,
    FLOOR1,
    FLOOR2,
    FLOOR3,
    BASEMENT1,
    BASEMENT2,
    BASEMENT3,
    BETWEEN_LEVELS,
}
