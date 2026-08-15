package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Angel's Tomb
 * {3}
 * Artifact — Uncommon (AVR #211)
 *
 * "Whenever a creature you control enters, you may have this artifact become a 3/3 white Angel
 * artifact creature with flying until end of turn."
 *
 * Implementation notes:
 *  - The trigger uses [TriggerBinding.ANY] over "creature you control", not the `OtherCreatureEnters`
 *    sugar: the printed text is "a creature you control", with no "another" clause. The distinction
 *    is inert in practice (the Tomb is not a creature as it enters) but the ANY binding is what the
 *    text says.
 *  - [MayEffect] wraps the animation: the controller is asked each time the trigger resolves, and
 *    declining leaves the Tomb a plain artifact.
 *  - [Effects.BecomeCreature] adds CREATURE on top of the printed ARTIFACT type (so it stays an
 *    *artifact* creature), sets base 3/3, the Angel subtype, white, and flying, all until end of
 *    turn. Animating an already-animated Tomb just layers a second identical effect — harmless.
 */
val AngelsTomb = card("Angel's Tomb") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a creature you control enters, you may have this artifact become a 3/3 " +
        "white Angel artifact creature with flying until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = Filters.Creature.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = MayEffect(
            Effects.BecomeCreature(
                target = EffectTarget.Self,
                power = 3,
                toughness = 3,
                keywords = setOf(Keyword.FLYING),
                creatureTypes = setOf("Angel"),
                addTypes = setOf(CardType.CREATURE.name),
                colors = setOf(Color.WHITE.name),
                duration = Duration.EndOfTurn
            ),
            descriptionOverride = "You may have Angel's Tomb become a 3/3 white Angel artifact " +
                "creature with flying until end of turn",
            inlineOnTrigger = true
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "Dan Murayama Scott"
        flavorText = "\"Faith can quicken the stones themselves with life.\"\n—Writings of Mikaeus"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28226303-7e67-4b88-adae-2386aff033ec.jpg?1783940655"
    }
}
