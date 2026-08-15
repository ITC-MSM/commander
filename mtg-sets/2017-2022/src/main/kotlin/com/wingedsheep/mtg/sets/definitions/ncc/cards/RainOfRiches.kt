package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate

/**
 * Rain of Riches {3}{R}{R}
 * Enchantment
 *
 * When this enchantment enters, create two Treasure tokens.
 * The first spell you cast each turn that mana from a Treasure was spent to cast has
 * cascade.
 *
 * "Has cascade" is modeled as a triggered ability on Rain of Riches (same shape as
 * Wildsear, Scouring Maw): whenever the controller casts a spell paid for with treasure
 * mana, and it's the first such spell this turn, fire [com.wingedsheep.sdk.dsl.Effects.Cascade]
 * pointing at the triggering spell. Cascade itself is a triggered ability (CR 702.85a),
 * so the resulting game behavior matches "the spell has cascade" without needing to grant
 * the keyword to the spell on the stack.
 */
val RainOfRiches = card("Rain of Riches") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create two Treasure tokens.\n" +
        "The first spell you cast each turn that mana from a Treasure was spent to cast " +
        "has cascade. (When you cast the spell, exile cards from the top of your library " +
        "until you exile a nonland card that costs less. You may cast it without paying " +
        "its mana cost. Put the exiled cards on the bottom in a random order.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateTreasure(2)
    }

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            requires = setOf(SpellCastPredicate.PaidWithManaFromSubtype(Subtype.TREASURE)),
        )
        triggerCondition = Conditions.IsFirstSpellPaidWithTreasureManaCastThisTurn
        effect = Effects.Cascade
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "50"
        artist = "Evyn Fong"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c5241c1-ffe9-4490-b4df-7e91c372c27c.jpg?1673482136"
    }
}
