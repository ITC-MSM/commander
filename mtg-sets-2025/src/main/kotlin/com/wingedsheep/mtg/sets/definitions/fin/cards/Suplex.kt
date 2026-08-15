package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.MarkExileOnDeathEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetPermanent


/**
 * Suplex
 * {1}{R}
 * Sorcery
 * Choose one —
 * • Suplex deals 3 damage to target creature. If that creature would die this turn, exile it instead.
 * • Exile target artifact.
 */
val Suplex = card("Suplex") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n• Suplex deals 3 damage to target creature. If that creature would die this turn, exile it instead.\n• Exile target artifact."
    spell {
        modal(chooseCount = 1) {
            mode("Suplex deals 3 damage to target creature. If that creature would die this turn, exile it instead") {
                val t = target("target", TargetCreature(filter = TargetFilter.Creature))
                effect = Effects.Composite(
                    DealDamageEffect(3, t),
                    MarkExileOnDeathEffect(t)
                )
            }
            mode("Exile target artifact") {
                val t = target("target", TargetPermanent(filter = TargetFilter.Artifact))
                effect = Effects.Exile(t)
            }
        }
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Fang Xinyu"
        flavorText = "\"The time has come to put my training to use!\""
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f61693a2-7042-44e0-85ba-9bf12ab94e7e.jpg?1748706376"
    }
}
