package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Unstoppable Ogre — Kamigawa: Neon Dynasty #169 (canonical printing)
 * {2}{R} · Artifact Creature — Ogre Warrior · 4/1
 *
 * When this creature enters, target creature can't block this turn.
 */
val UnstoppableOgre = card("Unstoppable Ogre") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Ogre Warrior"
    power = 4
    toughness = 1
    oracleText = "When this creature enters, target creature can't block this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("creature that can't block", TargetCreature())
        effect = Effects.CantBlock(t)
        description = "When this creature enters, target creature can't block this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Xavier Ribeiro"
        flavorText = "Touma never met a door he didn't hate."
        imageUri = "https://cards.scryfall.io/normal/front/7/1/719f20d9-2baa-49b3-8c6a-89f21a07d538.jpg?1783923857"
    }
}
