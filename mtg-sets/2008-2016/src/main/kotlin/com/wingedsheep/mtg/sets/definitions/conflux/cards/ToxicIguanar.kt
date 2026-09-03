package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Toxic Iguanar — Conflux #72
 * {R} · Creature — Lizard · 1/1
 *
 * This creature has deathtouch as long as you control a green permanent.
 *
 * A Conflux "domain-lite" common: the grant is a [ConditionalStaticAbility] wrapping a
 * [GrantKeyword] over [Filters.Self] (the source permanent), gated on
 * `Conditions.YouControl(GameObjectFilter.Permanent.withColor(...))` — an `Exists` over your
 * battlefield, which is the SDK spelling of "as long as you control a green permanent". A bare
 * "permanent" filter, not a creature one: any green permanent turns the deathtouch on.
 */
val ToxicIguanar = card("Toxic Iguanar") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard"
    power = 1
    toughness = 1
    oracleText = "This creature has deathtouch as long as you control a green permanent. " +
        "(Any amount of damage a creature with deathtouch deals to a creature is enough to destroy it.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.DEATHTOUCH, Filters.Self),
            condition = Conditions.YouControl(GameObjectFilter.Permanent.withColor(Color.GREEN))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Brandon Kitkouski"
        flavorText = "There are no \"weak\" creatures on Jund. Even the smallest can strike a deadly blow."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28fd2dce-b91f-441f-a3ea-af87cc925713.jpg"
    }
}
