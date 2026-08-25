package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Gate
import com.wingedsheep.sdk.scripting.effects.GatedEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Brass Gnat
 * {1}
 * Artifact Creature — Insect
 * 1/1
 * Flying
 * This creature doesn't untap during your untap step.
 * At the beginning of your upkeep, you may pay {1}. If you do, untap this creature.
 *
 * The upkeep tax is the whole card: the wind-down is [AbilityFlag.DOESNT_UNTAP] (the engine's
 * untap-step skip, shared with Galvanic Juggernaut and Leviathan) and the winding is a
 * [Gate.MayPay] — "you may pay {1}. If you do" is one resolution, payment and payoff together,
 * not a reflexive trigger.
 *
 * Assay's `compile` model does not carry the "doesn't untap" line (it emits only the upkeep
 * trigger and Flying); `flags` sits outside the differential's compared surface, so the flag is
 * authored here to keep the card behaviourally right.
 */
val BrassGnat = card("Brass Gnat") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "This creature doesn't untap during your untap step.\n" +
        "At the beginning of your upkeep, you may pay {1}. If you do, untap this creature."

    keywords(Keyword.FLYING)
    flags(AbilityFlag.DOESNT_UNTAP)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = GatedEffect(
            gate = Gate.MayPay(PayManaCostEffect(ManaCost.parse("{1}"))),
            then = Effects.Untap(EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Martina Pilcerova"
        flavorText = "Its buzzing is the sound of its inner mechanisms, ever winding down."
        imageUri = "https://cards.scryfall.io/normal/front/3/8/386ae7c6-347c-4b29-b7a9-3ca3bb050396.jpg"
    }
}
