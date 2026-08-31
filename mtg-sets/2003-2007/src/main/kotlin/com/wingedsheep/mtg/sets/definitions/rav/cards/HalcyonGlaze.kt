package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Halcyon Glaze
 * {1}{U}{U}
 * Enchantment
 * Whenever you cast a creature spell, this enchantment becomes a 4/4 Illusion creature with flying
 * in addition to its other types until end of turn.
 *
 * "In addition to its other types" is [Effects.BecomeCreature]'s default: it adds CREATURE and
 * leaves the printed types alone, so the animated Halcyon Glaze is an Enchantment Creature — an
 * enchantment-removal spell still answers it, and it still counts as an enchantment for constellation
 * and the like.
 *
 * The trigger resolves *before* the creature spell that caused it (both are on the stack, the
 * trigger on top), so the animation is already live when the creature enters. It fires on the cast,
 * not the resolution — countering the creature spell doesn't undo it.
 */
val HalcyonGlaze = card("Halcyon Glaze") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever you cast a creature spell, this enchantment becomes a 4/4 Illusion " +
        "creature with flying in addition to its other types until end of turn."

    triggeredAbility {
        trigger = Triggers.YouCastCreature
        effect = Effects.BecomeCreature(
            target = EffectTarget.Self,
            power = 4,
            toughness = 4,
            keywords = setOf(Keyword.FLYING),
            creatureTypes = setOf("Illusion"),
            duration = Duration.EndOfTurn
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "54"
        artist = "John Avon"
        flavorText = "\"Earth to æther, window to wonder, stillness to the sky.\"\n—Glazing incantation"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fafd3018-1cfe-4c41-a08c-5c2ba3528c7e.jpg?1783943684"
    }
}
