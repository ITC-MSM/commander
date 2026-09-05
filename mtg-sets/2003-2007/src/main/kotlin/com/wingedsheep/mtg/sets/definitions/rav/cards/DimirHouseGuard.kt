package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val DimirHouseGuard = card("Dimir House Guard") {
    manaCost = "{3}{B}"
    typeLine = "Creature — Skeleton"
    oracleText = "Fear (This creature can't be blocked except by artifact creatures and/or black creatures.)\nSacrifice a creature: Regenerate this creature.\nTransmute {1}{B}{B} ({1}{B}{B}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "B"
    power = 2
    toughness = 3

    keywords(Keyword.FEAR)
    activatedAbility {
        cost = Costs.Sacrifice(GameObjectFilter.Creature)
        effect = Effects.Regenerate(EffectTarget.Self)
    }
    transmute("{1}{B}{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "John Zeleznik"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a021caf-d9e7-470b-85be-3af42a3adfd3.jpg?1783943672"
    }
}
