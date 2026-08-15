package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ancient Tomb
 * Land
 *
 * {T}: Add {C}{C}. This land deals 2 damage to you.
 */
val AncientTomb = card("Ancient Tomb") {
    typeLine = "Land"
    oracleText = "{T}: Add {C}{C}. This land deals 2 damage to you."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(2)
            .then(Effects.DealDamage(2, EffectTarget.PlayerRef(Player.You)))
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "315"
        artist = "Colin MacNeil"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30e401e3-282b-4524-87e1-c6cd50cd6d00.jpg?1562053283"
    }
}
