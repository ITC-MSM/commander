package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Brave-Kin Duo {W}
 * Creature — Rabbit Mouse
 * 1/1
 *
 * {1}, {T}: Target creature gets +1/+1 until end of turn. Activate only as a sorcery.
 */
val BraveKinDuo = card("Brave-Kin Duo") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Rabbit Mouse"
    power = 1
    toughness = 1
    oracleText = "{1}, {T}: Target creature gets +1/+1 until end of turn. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, creature)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Devin Platts"
        flavorText = "\"Did you bring everything you need for the journey?\" the rabbit asked. \"Of course I did! I brought you, my dear pine cone,\" the mouse replied."
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8dd4693-424d-4d6e-86cf-24401a23d6b1.jpg?1721425770"
    }
}
