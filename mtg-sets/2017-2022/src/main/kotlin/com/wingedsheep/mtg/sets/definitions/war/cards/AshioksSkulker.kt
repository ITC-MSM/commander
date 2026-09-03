package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ashiok's Skulker — War of the Spark #40 (canonical printing)
 * {4}{U}
 * Creature — Nightmare
 * 3 / 5
 * {3}{U}: This creature can't be blocked this turn.
 *
 * "Can't be blocked" is an [AbilityFlag], not a [com.wingedsheep.sdk.core.Keyword], so the ability
 * is [Effects.GrantKeyword] over [AbilityFlag.CANT_BE_BLOCKED] with the default until-end-of-turn
 * duration. "This creature" is [EffectTarget.Self] — the ability is untargeted, which is why it
 * carries no target requirement (unlike Wormhole Serpent's otherwise identical
 * "{3}{U}: *Target* creature can't be blocked this turn").
 */
val AshioksSkulker = card("Ashiok's Skulker") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Nightmare"
    oracleText = "{3}{U}: This creature can't be blocked this turn."
    power = 3
    toughness = 5

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Livia Prima"
        flavorText = "\"Fear writhes and whispers in the shadows of your mind. It is the enemy you always knew would come.\"\n—Ashiok"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7dd5b0a1-104d-4e0f-82de-65487fbf01ff.jpg"
    }
}
