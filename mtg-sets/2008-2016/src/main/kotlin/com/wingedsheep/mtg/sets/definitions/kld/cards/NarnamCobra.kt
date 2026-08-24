package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Narnam Cobra
 * {2}
 * Artifact Creature — Snake
 * 2/1
 * {G}: This creature gains deathtouch until end of turn.
 *
 * A colorless artifact creature with a colored activation cost, so `colorIdentity` is green even
 * though the card itself is colorless.
 */
val NarnamCobra = card("Narnam Cobra") {
    manaCost = "{2}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Snake"
    oracleText = "{G}: This creature gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy it.)"
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "224"
        artist = "Christopher Burdett"
        flavorText = "Some say tales of the Narnam greenhouse are told to scare the children, but every tale grows from a seed of truth."
        imageUri = "https://cards.scryfall.io/normal/front/8/8/88bf1a67-61f7-4f03-b677-a874b64c989e.jpg?1783937152"
    }
}
