package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bastion Mastodon
 * {5}
 * Artifact Creature — Elephant
 * 4/5
 * {W}: This creature gains vigilance until end of turn.
 *
 * The grant is untargeted — "this creature" is the source — so it is [Effects.GrantKeyword] on
 * [EffectTarget.Self], whose default `Duration.EndOfTurn` is the printed "until end of turn"
 * (Cloudheath Drake).
 */
val BastionMastodon = card("Bastion Mastodon") {
    manaCost = "{5}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Elephant"
    oracleText = "{W}: This creature gains vigilance until end of turn."
    power = 4
    toughness = 5

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "197"
        artist = "Victor Adame Minguez"
        flavorText = "The Consulate's automaton bank contains only the most impressive specimens."
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c3ea690f-fd4e-4d05-b815-22d97736e894.jpg?1783937162"
    }
}
