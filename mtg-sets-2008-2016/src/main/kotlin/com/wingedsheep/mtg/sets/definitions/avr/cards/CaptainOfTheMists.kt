package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Captain of the Mists
 * {2}{U}
 * Creature — Human Wizard
 * 2/3
 * Whenever another Human you control enters, untap this creature.
 * {1}{U}, {T}: You may tap or untap target permanent.
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
            filter = GameObjectFilter.Creature.withSubtype(Subtype.HUMAN).youControl(),
            binding = TriggerBinding.OTHER,
        )
        effect = Effects.Untap(EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        val t = target("target permanent", TargetPermanent())
        effect = MayEffect(
            Effects.ChooseAction(
                listOf(
                    EffectChoice("Tap it", Effects.Tap(t)),
                    EffectChoice("Untap it", Effects.Untap(t)),
                ),
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
