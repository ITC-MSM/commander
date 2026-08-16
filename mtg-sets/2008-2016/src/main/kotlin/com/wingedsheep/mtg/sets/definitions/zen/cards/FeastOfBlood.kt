package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Feast of Blood
 * {1}{B}
 * Sorcery
 * Cast this spell only if you control two or more Vampires.
 * Destroy target creature. You gain 4 life.
 *
 * "two or more Vampires" is a bare tribal noun, so it counts every Vampire *permanent* you
 * control, not only the creature ones. The restriction is a cast-time check
 * ([castOnlyIf]); it is not rechecked on resolution.
 */
val FeastOfBlood = card("Feast of Blood") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Cast this spell only if you control two or more Vampires.\nDestroy target creature. You gain 4 life."

    spell {
        castOnlyIf(Conditions.YouControlAtLeast(2, GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE)))

        val creature = target("creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.Destroy(creature),
            Effects.GainLife(4)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "88"
        artist = "Jason Felix"
        flavorText = "\"The vampires of this world don't know the pleasures of hunger. They gorge themselves without savoring the kill.\" —Sorin Markov"
        imageUri = "https://cards.scryfall.io/normal/front/1/a/1a7dd5e2-b2a5-46ab-a67c-499451706505.jpg?1783942154"
        ruling("2009-10-01", "Whether you control two or more Vampires is checked only as you try to move Feast of Blood to the stack as the first step of casting it. It doesn’t matter whether you still control two or more Vampires as you finish casting Feast of Blood (in case you somehow sacrifice one to produce mana) or as Feast of Blood resolves.")
        ruling("2009-10-01", "If the targeted creature is an illegal target by the time Feast of Blood resolves, the entire spell doesn’t resolve. You don’t gain life.")
    }
}
