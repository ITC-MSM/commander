package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Self-Assembler
 * {5}
 * Artifact Creature — Assembly-Worker
 * 4/4
 * When this creature enters, you may search your library for an Assembly-Worker creature card,
 * reveal it, put it into your hand, then shuffle.
 */
val SelfAssembler = card("Self-Assembler") {
    manaCost = "{5}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Assembly-Worker"
    power = 4
    toughness = 4
    oracleText = "When this creature enters, you may search your library for an Assembly-Worker creature card, reveal it, put it into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Creature.withSubtype("Assembly-Worker"),
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true,
                shuffleAfter = true
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "232"
        artist = "Noah Bradley"
        flavorText = "It sees itself in all of its creations."
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d05c6c2-4bb3-468a-b23c-b0425a9982f1.jpg?1783937148"

        ruling("2018-03-16", "Self-Assembler's ability can find any creature card with the Assembly-Worker subtype, not only creature cards named Assembly-Worker. Notably, it can't find Mishra's Factory.")
    }
}
