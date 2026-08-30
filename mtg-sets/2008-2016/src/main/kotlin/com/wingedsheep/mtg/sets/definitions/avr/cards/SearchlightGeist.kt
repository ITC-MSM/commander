package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Searchlight Geist
 * {2}{B}
 * Creature — Spirit
 * 2 / 1
 *
 * Flying
 * {3}{B}: This creature gains deathtouch until end of turn. (Any amount of damage it deals to a
 * creature is enough to destroy it.)
 *
 * [Effects.GrantKeyword] on [EffectTarget.Self]; its default duration is already until end of turn.
 */
val SearchlightGeist = card("Searchlight Geist") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 1
    oracleText = "Flying\n" +
        "{3}{B}: This creature gains deathtouch until end of turn. (Any amount of damage it deals to a " +
        "creature is enough to destroy it.)"

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{3}{B}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Steven Belledin"
        flavorText = "It rises with the fall of darkness, seeking souls to extinguish."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0dc1a94-0193-464e-a481-730b34b57db5.jpg?1783940691"
    }
}
