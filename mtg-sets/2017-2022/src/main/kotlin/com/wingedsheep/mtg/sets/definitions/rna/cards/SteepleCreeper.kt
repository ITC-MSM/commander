package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Steeple Creeper — Ravnica Allegiance #142
 * {2}{G} · Creature — Frog Snake · 4 / 2
 *
 * Off-colour activation granting flying to itself until end of turn.
 */
val SteepleCreeper = card("Steeple Creeper") {
    manaCost = "{2}{G}"
    colorIdentity = "GU"
    typeLine = "Creature — Frog Snake"
    power = 4
    toughness = 2
    oracleText = "{3}{U}: This creature gains flying until end of turn."

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        effect = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Svetlin Velinov"
        flavorText = "\"If the Fin Clade cannot produce a reliable venomous krasis, mobile in both air and water, then the Guardian Project will absorb its resources.\"\n" +
        "—Vannifar"
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4afafb4-3fc0-4ccf-a942-e4bd2f146d89.jpg"
    }
}
