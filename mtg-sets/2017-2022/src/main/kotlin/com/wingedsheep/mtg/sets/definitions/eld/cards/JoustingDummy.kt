package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Jousting Dummy
 * {2}
 * Artifact Creature — Scarecrow Knight
 * 2/1
 *
 * {3}: This creature gets +1/+0 until end of turn.
 */
val JoustingDummy = card("Jousting Dummy") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow Knight"
    oracleText = "{3}: This creature gets +1/+0 until end of turn."
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{3}: This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "224"
        artist = "Milivoj Ćeran"
        flavorText = "\"Don't let it fool you. Many of us got our first scars from Syr Nobody.\"\n—Syr Layne, knight of Embereth"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6601056-af08-4239-97d5-5e11597fce18.jpg?1783932584"
    }
}
