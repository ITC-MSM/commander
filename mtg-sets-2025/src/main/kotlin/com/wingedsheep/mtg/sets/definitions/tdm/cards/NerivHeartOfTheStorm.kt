package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.DoubleDamage
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.SourceFilter

/**
 * Neriv, Heart of the Storm — Tarkir: Dragonstorm #210
 * {1}{R}{W}{B} · Legendary Creature — Spirit Dragon · 4/5
 *
 * Flying
 * If a creature you control that entered this turn would deal damage, it deals twice
 * that much damage instead.
 *
 * The damage-doubling clause is a damage replacement effect ([DoubleDamage]) gated on the
 * damage source: a creature controlled by Neriv's controller that has the
 * [com.wingedsheep.sdk.scripting.predicates.StatePredicate.EnteredThisTurn] state predicate
 * (tracked by `EnteredThisTurnComponent`, cleared at the start of each turn). This covers any
 * damage the creature deals — combat or ability — and applies to Neriv itself if it entered
 * this turn. `youControl()` resolves against the replacement source's controller.
 */
val NerivHeartOfTheStorm = card("Neriv, Heart of the Storm") {
    manaCost = "{1}{R}{W}{B}"
    colorIdentity = "RWB"
    typeLine = "Legendary Creature — Spirit Dragon"
    power = 4
    toughness = 5
    oracleText = "Flying\n" +
        "If a creature you control that entered this turn would deal damage, it deals twice " +
        "that much damage instead."

    keywords(Keyword.FLYING)

    replacementEffect(
        DoubleDamage(
            appliesTo = EventPattern.DamageEvent(
                source = SourceFilter.Matching(
                    GameObjectFilter.Creature.youControl().enteredThisTurn()
                )
            )
        )
    )

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "210"
        artist = "Victor Adame Minguez"
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b58112b0-a05c-4b98-b650-fd27ad97789f.jpg?1743204826"
    }
}
