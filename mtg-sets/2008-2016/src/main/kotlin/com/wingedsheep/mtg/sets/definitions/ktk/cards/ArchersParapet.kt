package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Archers' Parapet
 * {1}{G}
 * Creature — Wall
 * 0/5
 * Defender
 * {1}{B}, {T}: Each opponent loses 1 life.
 */
val ArchersParapet = card("Archers' Parapet") {
    manaCost = "{1}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 5
    oracleText = "Defender\n{1}{B}, {T}: Each opponent loses 1 life."

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.Tap)
        effect = LoseLifeEffect(1, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Wayne Reynolds"
        flavorText = "Every shaft is graven with a name from a kin tree, calling upon the spirits of the ancestors to make it fly true."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64ac0667-8ecc-4034-89bb-dce0af531014.jpg?1562787707"
    }
}
