package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Perplex
 * {1}{U}{B}
 * Instant
 *
 * Counter target spell unless its controller discards their hand.
 * Transmute {1}{U}{B}
 *
 * A [PayOrSufferEffect] whose payer is the *target spell's* controller
 * ([EffectTarget.TargetController]) and whose suffer half is a real counter, so an uncounterable
 * spell is untouched and "whenever a spell is countered" triggers still fire.
 *
 * The cost is [Costs.pay.DiscardHand] rather than a counted discard: the number is whatever they
 * hold at resolution, and an empty hand pays it for free (CR 118.3) — so a hellbent opponent
 * always saves their spell, which is the card's known weakness rather than a modelling shortcut.
 */
val Perplex = card("Perplex") {
    manaCost = "{1}{U}{B}"
    typeLine = "Instant"
    oracleText = "Counter target spell unless its controller discards their hand.\nTransmute {1}{U}{B} ({1}{U}{B}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "UB"

    spell {
        target("target spell", Targets.Spell)
        effect = PayOrSufferEffect(
            cost = Costs.pay.DiscardHand,
            suffer = Effects.CounterSpell(),
            player = EffectTarget.TargetController,
            // The payer is the opponent, so the generated clause ("counter target spell") would
            // read as an instruction addressed to Perplex's controller instead of a consequence.
            consequenceDescription = "let your spell be countered"
        )
    }
    transmute("{1}{U}{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "217"
        artist = "Tsutomu Kawade"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0db57459-29f0-4ef6-b256-56955036c0ef.jpg?1783943616"
    }
}
