package com.wingedsheep.mtg.sets.definitions.wth.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Thunderbolt
 * {1}{R}
 * Instant
 *
 * Choose one —
 * - Thunderbolt deals 3 damage to target player or planeswalker.
 * - Thunderbolt deals 4 damage to target creature with flying.
 *
 * An ordinary "choose one" — two modes, one pick, each carrying its own target. The second mode is
 * narrowed to creatures *with flying*, which the target legality check reads off projected state,
 * so a creature that lost flying this turn is no longer a legal choice.
 */
val Thunderbolt = card("Thunderbolt") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Thunderbolt deals 3 damage to target player or planeswalker.\n" +
        "• Thunderbolt deals 4 damage to target creature with flying."

    spell {
        modal {
            mode("Thunderbolt deals 3 damage to target player or planeswalker") {
                val t = target("target", Targets.PlayerOrPlaneswalker)
                effect = Effects.DealDamage(3, t)
            }
            mode("Thunderbolt deals 4 damage to target creature with flying") {
                val flier = target(
                    "target",
                    TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING)))
                )
                effect = Effects.DealDamage(4, flier)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Dylan Martens"
        flavorText = "\"Most wizards consider a thunderbolt to be a proper retort.\"\n—Ertai, wizard adept"
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0a4b641-2eb3-482b-91a1-236ebe2a7a41.jpg?1783946723"
    }
}
