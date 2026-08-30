package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Boggart Loggers
 * {2}{B}
 * Creature — Goblin Rogue
 * 2/1
 * Forestwalk
 * {2}{B}, Sacrifice this creature: Destroy target Treefolk or Forest.
 *
 * The printed target names two subtypes and no card type — a Treefolk is any permanent with that
 * creature type and a Forest is any land with that land type — so this is one
 * [GameObjectFilter.Permanent] with a [GameObjectFilter.withAnySubtype] union, not two clauses and not
 * a creature-typed filter. An animated Forest satisfies both halves; either is enough.
 */
val BoggartLoggers = card("Boggart Loggers") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Rogue"
    power = 2
    toughness = 1
    oracleText = "Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)\n" +
        "{2}{B}, Sacrifice this creature: Destroy target Treefolk or Forest."

    keywords(Keyword.FORESTWALK)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{B}"), Costs.SacrificeSelf)
        val treefolkOrForest = target(
            "target Treefolk or Forest",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.withAnySubtype(Subtype.TREEFOLK.value, Subtype.FOREST.value)
                )
            )
        )
        effect = Effects.Destroy(treefolkOrForest)
        description = "Destroy target Treefolk or Forest."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Jesper Ejsing"
        flavorText = "\"Auntie Flint lent axes to Nibb and Gyik, thinking they'd share their experiences with her. She's still waiting for them to come back.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9ed9a638-c3f9-48dd-9633-5c52146e0dcd.jpg?1783942893"
    }
}
