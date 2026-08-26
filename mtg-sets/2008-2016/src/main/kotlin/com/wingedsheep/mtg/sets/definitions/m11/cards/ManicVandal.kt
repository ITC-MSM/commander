package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Manic Vandal — Magic 2011 #151
 * {2}{R} · Creature — Human Warrior · 2 / 2
 *
 * When this creature enters, destroy target artifact.
 *
 * The Oxidda Scrapmelter shape: a SELF-bound [Triggers.EntersBattlefield] over [Effects.Destroy],
 * which lowers to a graveyard move flagged `byDestruction` so indestructible and regeneration see
 * it. The trigger is not optional and its target is not "up to", so it must pick an artifact when
 * one is on the battlefield — including one of yours when the opponent has none.
 */
val ManicVandal = card("Manic Vandal") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, destroy target artifact."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val artifact = target("target artifact", Targets.Artifact)
        effect = Effects.Destroy(artifact)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "151"
        artist = "Christopher Moeller"
        flavorText = "It's fun. He doesn't need another reason."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a503697a-4940-4b8f-98b1-5ea9151866fa.jpg?1783941803"
    }
}
