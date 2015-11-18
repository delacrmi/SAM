package com.delacrmi.persistences;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by miguel on 17/11/15.
 */
public class EntityRelation {

    private List<Vector<EntityColumn>> relations = new ArrayList<Vector<EntityColumn>>();
    private Class tableRelation;
    private RelationType relation;

    public List<Vector<EntityColumn>> getRelations() {
        return relations;
    }

    public EntityRelation addColumnRelation(EntityColumn inTable, EntityColumn outTable) {

        Vector<EntityColumn> value = new Vector<EntityColumn>();
        value.add(inTable);
        value.add(outTable);
        relations.add(value);

        return this;
    }

    public enum RelationType {
        ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY;
    }
}
