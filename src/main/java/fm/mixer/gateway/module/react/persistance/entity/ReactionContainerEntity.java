package fm.mixer.gateway.module.react.persistance.entity;

import java.util.Set;

public interface ReactionContainerEntity<ITEM extends ReactionContainerEntity<ITEM, TABLE>, TABLE extends ReactionEntity<ITEM>> {

    Set<TABLE> getReactions();

    void setReactions(Set<TABLE> reactions);
}
