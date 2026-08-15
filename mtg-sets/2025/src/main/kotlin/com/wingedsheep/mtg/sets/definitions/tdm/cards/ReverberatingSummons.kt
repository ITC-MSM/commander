package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration

/**
 * Reverberating Summons
 * {1}{R}
 * Enchantment
 *
 * At the beginning of each combat, if you've cast two or more spells this turn, this enchantment
 * becomes a 3/3 Monk creature with haste in addition to its other types until end of turn.
 * {1}{R}, Discard your hand, Sacrifice this enchantment: Draw two cards.
 */
val ReverberatingSummons = card("Reverberating Summons") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "At the beginning of each combat, if you've cast two or more spells this turn, this enchantment becomes a 3/3 Monk creature with haste in addition to its other types until end of turn.\n" +
        "{1}{R}, Discard your hand, Sacrifice this enchantment: Draw two cards."

    triggeredAbility {
        trigger = Triggers.EachCombat
        triggerCondition = Conditions.YouCastSpellsThisTurn(atLeast = 2)
        // Additive: no removeTypes, so the permanent stays an Enchantment while also becoming a creature.
        effect = Effects.BecomeCreature(
            power = 3,
            toughness = 3,
            keywords = setOf(Keyword.HASTE),
            creatureTypes = setOf(Subtype.MONK.value),
            duration = Duration.EndOfTurn
        )
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{R}"),
            Costs.DiscardHand,
            Costs.SacrificeSelf
        )
        effect = Effects.DrawCards(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "117"
        artist = "Marco Gorlei"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1af19ce8-bc0c-420c-9e3b-9059b4df32cb.jpg?1743204431"
    }
}
