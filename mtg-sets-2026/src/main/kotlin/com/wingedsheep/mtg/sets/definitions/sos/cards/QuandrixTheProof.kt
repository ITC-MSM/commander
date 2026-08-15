package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate

/**
 * Quandrix, the Proof
 * {4}{G}{U}
 * Legendary Creature — Elder Dragon
 * 6/6
 *
 * Flying, trample
 * Cascade
 * Instant and sorcery spells you cast from your hand have cascade.
 *
 * Cascade is itself a "when you cast this spell" triggered ability (CR 702.85a), so both halves
 * are modelled as cast triggers feeding the shared [com.wingedsheep.sdk.scripting.effects.CascadeEffect]
 * executor (which reads the triggering spell's mana value to set the threshold):
 *  - Quandrix's own cascade fires on [Triggers.WhenYouCastThisSpell] (SELF), reading Quandrix's
 *    mana value.
 *  - The granted cascade fires whenever the controller casts an instant or sorcery spell from
 *    their hand, mirroring Wildsear, Scouring Maw. Granting the trigger rather than literally
 *    stamping the CASCADE keyword onto the stacked spell produces identical game behavior, and
 *    the from-hand restriction is expressed with [SpellCastPredicate.CastFromZone].
 */
val QuandrixTheProof = card("Quandrix, the Proof") {
    manaCost = "{4}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Legendary Creature — Elder Dragon"
    power = 6
    toughness = 6
    oracleText = "Flying, trample\n" +
        "Cascade (When you cast this spell, exile cards from the top of your library until you " +
        "exile a nonland card that costs less. You may cast it without paying its mana cost. Put " +
        "the exiled cards on the bottom in a random order.)\n" +
        "Instant and sorcery spells you cast from your hand have cascade."

    keywords(Keyword.FLYING, Keyword.TRAMPLE, Keyword.CASCADE)

    // Cascade — Quandrix's own cast trigger.
    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    // Instant and sorcery spells you cast from your hand have cascade.
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.InstantOrSorcery,
            requires = setOf(SpellCastPredicate.CastFromZone(Zone.HAND)),
        )
        effect = Effects.Cascade
        description = "Instant and sorcery spells you cast from your hand have cascade."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "218"
        artist = "Lucas Graciano"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/015afe31-af3c-4c9b-9997-d7c33b915a33.jpg?1775938517"
    }
}
