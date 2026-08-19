package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kithkin Daggerdare
 * {1}{G}
 * Creature — Kithkin Soldier
 * 1/1
 * {G}, {T}: Target attacking creature gets +2/+2 until end of turn.
 */
val KithkinDaggerdare = card("Kithkin Daggerdare") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Kithkin Soldier"
    power = 1
    toughness = 1
    oracleText = "{G}, {T}: Target attacking creature gets +2/+2 until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{G}"), Costs.Tap)
        val attacker = target("target attacking creature", Targets.AttackingCreature)
        effect = Effects.ModifyStats(2, 2, attacker)
        description = "{G}, {T}: Target attacking creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "223"
        artist = "Christopher Moeller"
        flavorText = "The kith dance their elaborate reels not merely to celebrate the events of their lives but to form an unbreakable bond of loyalty with their kin, a bond stronger than the fear of death itself."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90d0d7fd-a580-41af-8e9d-fee74094ec47.jpg?1783942860"
    }
}
