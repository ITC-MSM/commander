package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pestilent Kathari
 * {2}{B}
 * Creature — Bird Warrior
 * 1/1
 * Flying
 * Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)
 * {2}{R}: This creature gains first strike until end of turn.
 *
 * Two printed [Keyword]s plus one self-pump-shaped grant: [Effects.GrantKeyword] onto
 * [EffectTarget.Self]. The facade's default duration is already `Duration.EndOfTurn`, so the
 * printed "until end of turn" needs no argument of its own.
 */
val PestilentKathari = card("Pestilent Kathari") {
    manaCost = "{2}{B}"
    colorIdentity = "BR"
    typeLine = "Creature — Bird Warrior"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "Deathtouch (Any amount of damage this deals to a creature is enough to destroy it.)\n" +
        "{2}{R}: This creature gains first strike until end of turn."

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    activatedAbility {
        cost = Costs.Mana("{2}{R}")
        effect = Effects.GrantKeyword(Keyword.FIRST_STRIKE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Dave Kendall"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dba00df7-2c8d-435a-86d1-6bd7b7413585.jpg"
    }
}
