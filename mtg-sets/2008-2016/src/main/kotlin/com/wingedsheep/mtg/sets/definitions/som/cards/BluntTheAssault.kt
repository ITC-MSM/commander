package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Blunt the Assault — Scars of Mirrodin #113
 * {3}{G} · Instant
 *
 * You gain 1 life for each creature on the battlefield. Prevent all combat damage that would be
 * dealt this turn.
 *
 * "Each creature on the battlefield" is `AggregateBattlefield(Player.Each)` — every creature, no
 * matter who controls it — counted on resolution (CR 608.2). The prevention half is the plain
 * turn-wide combat shield, so it also blanks damage from creatures that entered after this
 * resolved.
 */
val BluntTheAssault = card("Blunt the Assault") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "You gain 1 life for each creature on the battlefield. Prevent all combat damage that would be dealt this turn."

    spell {
        effect = Effects.Composite(
            Effects.GainLife(DynamicAmount.AggregateBattlefield(Player.Each, GameObjectFilter.Creature)),
            Effects.PreventAllCombatDamage(),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "113"
        artist = "Matt Stewart"
        flavorText = "\"Much can be gained from the appearance of vulnerability.\"\n—Ezuri, renegade leader"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6ecff12a-37d5-4a7b-b615-4c5e3bd950bb.jpg?1783941719"
    }
}
