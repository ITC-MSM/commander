package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Wren's Run Vanquisher
 * {1}{G}
 * Creature — Elf Warrior
 * 3/3
 * As an additional cost to cast this spell, reveal an Elf card from your hand or pay {3}.
 * Deathtouch
 *
 * The archetype of Lorwyn's reveal-or-pay cycle: a 3/3 deathtouch for {1}{G} out of an Elf hand,
 * {3}{1}{G} otherwise. The Elf shown stays in hand (CR 701.20b).
 */
val WrensRunVanquisher = card("Wren's Run Vanquisher") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 3
    toughness = 3
    oracleText = "As an additional cost to cast this spell, reveal an Elf card from your hand or " +
        "pay {3}.\nDeathtouch (Any amount of damage this deals to a creature is enough to destroy it.)"

    additionalCost(
        Costs.additional.RevealFromHandOrPay(
            filter = GameObjectFilter.Any.withSubtype(Subtype.ELF),
            alternativeManaCost = "{3}"
        )
    )

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "245"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9c411a0-b8dd-4394-852a-a29dbcec953b.jpg?1783942854"
    }
}
