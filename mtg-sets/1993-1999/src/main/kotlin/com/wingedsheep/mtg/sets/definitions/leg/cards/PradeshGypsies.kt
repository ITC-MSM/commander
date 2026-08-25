package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pradesh Gypsies
 * {2}{G}
 * Creature — Human Nomad
 * 1/1
 *
 * {1}{G}, {T}: Target creature gets -2/-0 until end of turn.
 */
val PradeshGypsies = card("Pradesh Gypsies") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Nomad"
    power = 1
    toughness = 1
    oracleText = "{1}{G}, {T}: Target creature gets -2/-0 until end of turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-2, 0, creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "197"
        artist = "Quinton Hoover"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/0370330d-83d9-44d2-a1ed-c4827edc60fd.jpg?1783948046"
    }
}
