package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Deface — Ravnica Allegiance #98
 * {R} · Sorcery
 *
 * An ordinary "choose one" — two modes, one pick. The second mode's target is narrowed to
 * creatures *with defender*, which the target legality check reads off projected state, so a
 * creature that lost defender this turn is no longer a legal choice.
 */
val Deface = card("Deface") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Destroy target artifact.\n" +
        "• Destroy target creature with defender."

    spell {
        modal {
            mode("Destroy target artifact") {
                val artifact = target("target", Targets.Artifact)
                effect = Effects.Destroy(artifact)
            }
            mode("Destroy target creature with defender") {
                val wall = target(
                    "target",
                    TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withKeyword(Keyword.DEFENDER)))
                )
                effect = Effects.Destroy(wall)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Sidharth Chaturvedi"
        flavorText = "\"Leave no stone unturned.\"\n" +
        "—Ruric Thar"
        imageUri = "https://cards.scryfall.io/normal/front/4/3/43df9f41-944e-4cf3-ac80-524eadac221d.jpg"
    }
}
