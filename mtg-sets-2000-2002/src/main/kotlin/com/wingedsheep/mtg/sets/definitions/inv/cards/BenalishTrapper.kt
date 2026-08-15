package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Benalish Trapper
 * {1}{W}
 * Creature — Human Soldier
 * 1/2
 * {W}, {T}: Tap target creature.
 */
val BenalishTrapper = card("Benalish Trapper") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 2
    oracleText = "{W}, {T}: Tap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val t = target("target", TargetCreature())
        effect = TapUntapEffect(
            target = t,
            tap = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Ken Meyer, Jr."
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e312653d-c3e1-4c79-90d2-0963419b618c.jpg?1562940560"
    }
}
