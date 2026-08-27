package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Midnight Charm
 * {B}
 * Instant
 * Choose one —
 * • Midnight Charm deals 1 damage to target creature and you gain 1 life.
 * • Target creature gains first strike until end of turn.
 * • Tap target creature.
 */
val MidnightCharm = card("Midnight Charm") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Midnight Charm deals 1 damage to target creature and you gain 1 life.\n" +
        "• Target creature gains first strike until end of turn.\n" +
        "• Tap target creature."

    spell {
        modal(chooseCount = 1) {
            mode("Midnight Charm deals 1 damage to target creature and you gain 1 life") {
                val t = target("target", Targets.Creature)
                effect = Effects.DealDamage(1, t) then Effects.GainLife(1)
            }
            mode("Target creature gains first strike until end of turn") {
                val t = target("target", Targets.Creature)
                effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, t)
            }
            mode("Tap target creature") {
                val t = target("target", Targets.Creature)
                effect = Effects.Tap(t)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "John Avon"
        imageUri = "https://cards.scryfall.io/normal/front/3/3/33f51f18-41af-4a2c-a353-48bebd697599.jpg"
    }
}
