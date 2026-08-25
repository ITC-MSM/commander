package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Resounding Thunder
 * {2}{R}
 * Instant
 * Resounding Thunder deals 3 damage to any target.
 * Cycling {5}{B}{R}{G} ({5}{B}{R}{G}, Discard this card: Draw a card.)
 * When you cycle this card, it deals 6 damage to any target.
 *
 * The red member of the Alara "Resounding" cycle, composed like the Onslaught cycling cycle: a
 * `spell { }` body, [KeywordAbility.cycling] for the wedge-coloured cycling cost, and a
 * [Triggers.YouCycleThis] triggered ability carrying the larger effect. Both halves are
 * [Effects.DealDamage] over a [Targets].`Any` requirement declared on their own ability, and the
 * damage source is left implicit so it resolves to the card itself. Unlike the Onslaught cycle there
 * is no printed "you may", so the trigger is not optional.
 */
val ResoundingThunder = card("Resounding Thunder") {
    manaCost = "{2}{R}"
    colorIdentity = "BGR"
    typeLine = "Instant"
    oracleText = "Resounding Thunder deals 3 damage to any target.\n" +
        "Cycling {5}{B}{R}{G} ({5}{B}{R}{G}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, it deals 6 damage to any target."

    spell {
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(3, t)
    }

    keywordAbility(KeywordAbility.cycling("{5}{B}{R}{G}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(6, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Jon Foster"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/680b7955-d939-4195-aba8-b46a8c925616.jpg"
    }
}
