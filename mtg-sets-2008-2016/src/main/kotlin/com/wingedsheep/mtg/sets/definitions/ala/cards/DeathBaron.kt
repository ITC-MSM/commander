package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Death Baron
 * {1}{B}{B}
 * Creature — Zombie Wizard
 * 2/2
 * Skeletons you control and other Zombies you control get +1/+1 and have deathtouch.
 *
 * Modeled as two lord static abilities over the same group: every Skeleton or
 * Zombie you control, excluding Death Baron itself ("other Zombies"). Because
 * Death Baron is a Zombie (not a Skeleton), `excludeSelf = true` over a
 * Zombie-OR-Skeleton filter is rules-faithful — it drops only this permanent
 * from the Zombie clause while leaving every Skeleton unaffected.
 */
val DeathBaron = card("Death Baron") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Wizard"
    oracleText = "Skeletons you control and other Zombies you control get +1/+1 and have deathtouch. " +
        "(Any amount of damage they deal to a creature is enough to destroy it.)"
    power = 2
    toughness = 2

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.youControl().withAnySubtype("Zombie", "Skeleton"),
                excludeSelf = true
            )
        )
    }
    staticAbility {
        ability = GrantKeyword(
            Keyword.DEATHTOUCH,
            GroupFilter(
                GameObjectFilter.Creature.youControl().withAnySubtype("Zombie", "Skeleton"),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "70"
        artist = "Nils Hamm"
        flavorText = "For the necromancer barons, killing and recruitment are one and the same."
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d59b5e5-fc16-4f1a-9f17-f42908473531.jpg?1782715970"
    }
}
