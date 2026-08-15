package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect

/**
 * Thornscape Master
 * {2}{G}{G}
 * Creature — Human Wizard
 * 2/2
 * {R}{R}, {T}: This creature deals 2 damage to target creature.
 * {W}{W}, {T}: Target creature gains protection from the color of your choice until end of turn.
 */
val ThornscapeMaster = card("Thornscape Master") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{R}{R}, {T}: This creature deals 2 damage to target creature.\n" +
        "{W}{W}, {T}: Target creature gains protection from the color of your choice until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}{R}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = DealDamageEffect(2, t)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}{W}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.ChooseColorThen(Effects.GrantProtectionFromChosenColor(t))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "216"
        artist = "Larry Elmore"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e8f164d-3782-4eaa-a4db-ab7082d45ee7.jpg?1562920549"
    }
}
