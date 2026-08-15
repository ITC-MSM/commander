package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Monastery Messenger — Tarkir: Dragonstorm #208
 * {2/U}{2/R}{2/W} · Creature — Bird Scout · 2/3
 *
 * Flying, vigilance
 * When this creature enters, put up to one target noncreature, nonland card from your
 * graveyard on top of your library.
 *
 * "Up to one target" is `optional = true` on the [TargetObject]; the graveyard filter is a
 * noncreature, nonland card owned by you. The card is moved to the top of the library via
 * [Effects.PutOnTopOfLibrary].
 */
val MonasteryMessenger = card("Monastery Messenger") {
    manaCost = "{2/U}{2/R}{2/W}"
    colorIdentity = "URW"
    typeLine = "Creature — Bird Scout"
    power = 2
    toughness = 3
    oracleText = "Flying, vigilance\n" +
        "When this creature enters, put up to one target noncreature, nonland card from your graveyard on top of your library."

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val card = target(
            "noncreature, nonland card from your graveyard",
            TargetObject(
                optional = true,
                filter = TargetFilter(
                    GameObjectFilter.Nonland and GameObjectFilter.Noncreature.ownedByYou(),
                    zone = Zone.GRAVEYARD
                )
            )
        )
        effect = Effects.PutOnTopOfLibrary(card)
        description = "When this creature enters, put up to one target noncreature, nonland card from your graveyard on top of your library."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "208"
        artist = "Forrest Imel"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c9eeced-6464-41f0-bbea-05b3af4cc005.jpg?1743204819"
    }
}
