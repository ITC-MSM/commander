package com.wingedsheep.mtg.sets.definitions.ddp.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Retreat to Kazandu
 * {2}{G}
 * Enchantment
 * Landfall — Whenever a land you control enters, choose one —
 * • Put a +1/+1 counter on target creature.
 * • You gain 2 life.
 *
 * The canonical printing is Duel Decks: Zendikar vs. Eldrazi (2015-08-28), five weeks ahead of
 * Battle for Zendikar, which gets a [com.wingedsheep.sdk.model.Printing] row instead.
 *
 * A modal *triggered* ability: the mode is chosen as the ability goes on the stack (CR 603.3c).
 */
val RetreatToKazandu = card("Retreat to Kazandu") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Landfall — Whenever a land you control enters, choose one —\n" +
        "• Put a +1/+1 counter on target creature.\n" +
        "• You gain 2 life."

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = ModalEffect.chooseOne(
            Mode.withTarget(
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0)),
                TargetCreature(),
                "Put a +1/+1 counter on target creature",
            ),
            Mode.noTarget(
                Effects.GainLife(2),
                "You gain 2 life",
            ),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "21"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d3a089f-2049-406f-aa7e-2fdcbc59a826.jpg?1783938253"
    }
}
