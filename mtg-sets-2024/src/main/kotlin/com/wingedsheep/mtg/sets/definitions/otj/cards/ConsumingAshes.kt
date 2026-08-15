package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Consuming Ashes
 * {2}{B}{B}
 * Instant
 *
 * Exile target creature. If it had mana value 3 or less, surveil 2.
 *
 * The exile is unconditional; the surveil is gated on the targeted creature's mana value. The mana
 * value test reads the creature's last-known characteristic, so it resolves correctly even though
 * the creature has already been exiled when the conditional runs.
 */
val ConsumingAshes = card("Consuming Ashes") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature. If it had mana value 3 or less, surveil 2. " +
        "(Look at the top two cards of your library, then put any number of them into your " +
        "graveyard and the rest on top of your library in any order.)"

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.Exile(creature).then(
            ConditionalEffect(
                condition = Conditions.TargetSpellManaValueAtMost(DynamicAmount.Fixed(3)),
                effect = Patterns.Library.surveil(2)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Campbell White"
        flavorText = "The enforcer only got halfway through the arrest warrant before it—and he—went up in smoke."
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54f96be9-60fc-4e2f-9172-4cc53c9a095a.jpg?1712355566"
    }
}
