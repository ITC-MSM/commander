package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Captain of the Mists
 * {2}{U}
 * Creature — Human Wizard
 * 2/3
 * Whenever another Human you control enters, untap this creature.
 * {1}{U}, {T}: You may tap or untap target permanent.
 *
 * "You may tap or untap target permanent" is the corpus idiom for the clause — a [MayEffect] over a
 * two-[Mode] [ModalEffect] with `countsAsModalSpell = false`, shared with Pestermite, Stonybrook
 * Angler, Merrow Reejerey, Granite Witness, Sewer-veillance Cam, Elite Interceptor and Inverted
 * Iceberg. The target is locked in when the ability goes on the stack; the tap-or-untap choice is
 * made on resolution, so a responding tap does not strand the controller on the useless half.
 *
 * It was written as an `Effects.ChooseAction` over two `EffectChoice`s until Argentum Assay learned
 * to read the clause and the differential put the two spellings side by side. `ChooseActionEffect`
 * filters infeasible options and auto-selects when one remains, which quietly takes away a choice
 * the rules leave open — tapping an already-tapped permanent is legal and simply does nothing.
 */
val CaptainOfTheMists = card("Captain of the Mists") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText =
        "Whenever another Human you control enters, untap this creature.\n" +
            "{1}{U}, {T}: You may tap or untap target permanent."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            // "another **Human** you control", not "another Human creature": a bare creature-type
            // noun is a permanent noun (CR 205.3), so a Kindred permanent carrying the subtype
            // counts. The same migration that moved 104 filters off `Creature.withSubtype` missed
            // this one; the differential found it once Assay could read the card's other line.
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.HUMAN).youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.Untap(EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        val t = target("target permanent", TargetPermanent())
        effect = MayEffect(
            ModalEffect(
                modes = listOf(
                    Mode.noTarget(TapUntapEffect(t, tap = true), "Tap that permanent"),
                    Mode.noTarget(TapUntapEffect(t, tap = false), "Untap that permanent"),
                ),
                chooseCount = 1,
                countsAsModalSpell = false,
            ),
            descriptionOverride = "You may tap or untap that permanent",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "45"
        artist = "Allen Williams"
        flavorText =
            "\"I am no mere ship's captain. The north wind is my accomplice. The tide is my first mate.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/c/4/c43aa68e-a182-4006-b4d6-b4fc67e68583.jpg?1783940723"
    }
}
