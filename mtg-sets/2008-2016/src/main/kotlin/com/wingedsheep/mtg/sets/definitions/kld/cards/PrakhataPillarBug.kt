package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Prakhata Pillar-Bug
 * {3}
 * Artifact Creature — Insect
 * 2/3
 * {B}: This creature gains lifelink until end of turn.
 *
 * A colorless artifact creature with a colored activation cost, so `colorIdentity` is black even
 * though the card itself is colorless.
 */
val PrakhataPillarBug = card("Prakhata Pillar-Bug") {
    manaCost = "{3}"
    colorIdentity = "B"
    typeLine = "Artifact Creature — Insect"
    oracleText = "{B}: This creature gains lifelink until end of turn. (Damage dealt by this creature also causes you to gain that much life.)"
    power = 2
    toughness = 3

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "228"
        artist = "Jason Felix"
        flavorText = "Not a mote of dust escapes the dutiful cleaners that keep the exclusive Prakhata Club immaculate."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c91b356-b5d8-4239-bb45-dec7f673868d.jpg?1783937151"
    }
}
