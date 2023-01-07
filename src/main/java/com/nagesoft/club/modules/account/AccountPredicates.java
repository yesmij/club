package com.nagesoft.club.modules.account;

import com.nagesoft.club.modules.tag.Tag;
import com.nagesoft.club.modules.zone.Zone;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates {
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tagSet.any().in(tags));
    }
}
