package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sandstone Warrior
 * {2}{R}{R}
 * Creature — Human Soldier Warrior
 * 1/3
 * First strike (This creature deals combat damage before creatures without first strike.)
 * {R}: This creature gets +1/+0 until end of turn.
 */
val SandstoneWarrior = card("Sandstone Warrior") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Soldier Warrior"
    power = 1
    toughness = 3
    oracleText = "First strike (This creature deals combat damage before creatures without first strike.)\n" +
        "{R}: This creature gets +1/+0 until end of turn."

    keywords(Keyword.FIRST_STRIKE)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "Stephen Daniele"
        flavorText = "\"I used to describe something stable as 'rock solid.' So much for *that* expression.\"\n" +
            "—Gerrard of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/eaa61413-3c6a-4895-b8e7-2723e273a952.jpg"
    }
}
