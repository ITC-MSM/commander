package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Timber Protector
 * {4}{G}
 * Creature — Treefolk Warrior
 * 4/6
 * Other Treefolk creatures you control get +1/+1.
 * Other Treefolk and Forests you control have indestructible.
 *
 * The two lines deliberately use different filters, and the printed text is what separates them:
 * "Treefolk **creatures**" is the adjectival form (creatures only), while the bare "Treefolk and
 * Forests" names every *permanent* with either subtype — so an animated Forest, or a Treefolk that
 * isn't a creature, is indestructible but gets no +1/+1.
 *
 * `excludeSelf` on both is the "Other" in the text, and it is also what the 2013-07-01 ruling
 * turns on: if Timber Protector itself somehow becomes a Forest, it still doesn't grant itself
 * indestructible.
 */
val TimberProtector = card("Timber Protector") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Warrior"
    power = 4
    toughness = 6
    oracleText = "Other Treefolk creatures you control get +1/+1.\n" +
        "Other Treefolk and Forests you control have indestructible."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.youControl().withSubtype(Subtype.TREEFOLK),
                excludeSelf = true
            )
        )
    }

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.INDESTRUCTIBLE,
            filter = GroupFilter(
                GameObjectFilter.Permanent.youControl()
                    .withAnySubtype(Subtype.TREEFOLK.value, Subtype.FOREST.value),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "238"
        artist = "Terese Nielsen & Philip Tan"
        flavorText = "In his presence, an ordinary grove becomes a bastion to turn spells and break armies."
        imageUri = "https://cards.scryfall.io/normal/front/1/4/14b93784-cbe5-437a-8235-f6864e413b41.jpg?1783942856"
        ruling("2013-07-01", "If Timber Protector somehow becomes a Forest, it doesn't give itself indestructible.")
    }
}
