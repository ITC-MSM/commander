package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Stormscape Master
 * {2}{U}{U}
 * Creature — Human Wizard
 * 2/2
 * {W}{W}, {T}: Target creature gains protection from the color of your choice until end of turn.
 * {B}{B}, {T}: Target player loses 2 life and you gain 2 life.
 */
val StormscapeMaster = card("Stormscape Master") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{W}{W}, {T}: Target creature gains protection from the color of your choice until end of turn.\n" +
        "{B}{B}, {T}: Target player loses 2 life and you gain 2 life."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}{W}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.ChooseColorThen(Effects.GrantProtectionFromChosenColor(t))
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}{B}"), Costs.Tap)
        val t = target("target", TargetPlayer())
        effect = Effects.Composite(
            Effects.LoseLife(2, t),
            Effects.GainLife(2, EffectTarget.Controller)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "76"
        artist = "Hannibal King"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b704165-4587-48f1-8830-c5a07ec666cc.jpg?1562926376"
    }
}
