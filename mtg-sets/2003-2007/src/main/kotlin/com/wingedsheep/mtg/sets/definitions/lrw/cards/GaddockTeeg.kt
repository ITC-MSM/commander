package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.PlayersCantCastSpells
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Gaddock Teeg
 * {G}{W}
 * Legendary Creature — Kithkin Advisor
 * 2/2
 * Noncreature spells with mana value 4 or greater can't be cast.
 * Noncreature spells with {X} in their mana costs can't be cast.
 *
 * Two [PlayersCantCastSpells] statics rather than one: the printed card is two separate clauses over
 * two unrelated properties of the cost, and the primitive's `spellFilter` is a conjunction, so a
 * single filter could not express their union. `affected = Player.Each` is the "can't be cast"
 * (rather than "your opponents can't cast") wording — the lock binds Teeg's controller too.
 *
 * Both clauses read the *printed* cost off the card being cast, which is what the two rulings ask
 * for and what the filter predicates already do:
 *  - `manaValueAtLeast(4)` is the card's mana value, so alternative costs, additional costs and cost
 *    reductions don't move it (2018-12-07 ruling).
 *  - `hasXInManaCost()` inspects the printed cost's `{X}` symbol rather than a chosen value, so it
 *    catches an {X} spell in hand — where X is still 0 (CR 202.3b) and the first clause would miss
 *    it. That is exactly why the card prints two clauses.
 *
 * Lands are unaffected: playing a land is not casting a spell.
 */
val GaddockTeeg = card("Gaddock Teeg") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Kithkin Advisor"
    power = 2
    toughness = 2
    oracleText = "Noncreature spells with mana value 4 or greater can't be cast.\n" +
        "Noncreature spells with {X} in their mana costs can't be cast."

    staticAbility {
        ability = PlayersCantCastSpells(
            affected = Player.Each,
            spellFilter = GameObjectFilter.Any.notCreature().manaValueAtLeast(4)
        )
    }

    staticAbility {
        ability = PlayersCantCastSpells(
            affected = Player.Each,
            spellFilter = GameObjectFilter.Any.notCreature().hasXInManaCost()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "248"
        artist = "Greg Staples"
        flavorText = "So great is his wisdom and spirit that many who have met him say that they " +
            "stood before a giant of a man and talked to the wisest of the four winds."
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32c16e1b-f4ce-409f-928a-42c666adac9d.jpg?1783942856"
        ruling(
            "2018-12-07",
            "A spell's mana value is determined only by its mana cost. Alternative costs (including " +
                "casting a spell \"without paying its mana cost\"), additional costs, and cost " +
                "reductions don't affect a spell's mana value."
        )
        ruling(
            "2007-10-01",
            "If one half of a split card has a mana value of 3 or less and doesn't have an {X} in " +
                "its mana cost, Gaddock Teeg lets you cast that half."
        )
    }
}
