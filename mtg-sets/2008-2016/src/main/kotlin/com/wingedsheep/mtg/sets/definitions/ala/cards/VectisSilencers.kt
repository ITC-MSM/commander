package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vectis Silencers
 * {2}{U}
 * Artifact Creature — Human Rogue
 * 1 / 2
 * {2}{B}: This creature gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy that creature.)
 *
 * An Esper common with an off-colour activation: a plain [Costs.Mana]`("{2}{B}")` ability over
 * [Effects.GrantKeyword] bound to [EffectTarget.Self], whose default `Duration.EndOfTurn` is the
 * printed "until end of turn". The black in the activation cost is why the card's colour identity
 * is BU while its own colour is blue.
 */
val VectisSilencers = card("Vectis Silencers") {
    manaCost = "{2}{U}"
    colorIdentity = "BU"
    typeLine = "Artifact Creature — Human Rogue"
    power = 1
    toughness = 2
    oracleText = "{2}{B}: This creature gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy that creature.)"

    activatedAbility {
        cost = Costs.Mana("{2}{B}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Steven Belledin"
        flavorText = "Even on Esper, there are those who eschew the use of magic in favor of simpler methods."
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c0198220-073f-4479-a1c4-1bd626891e28.jpg"
    }
}
