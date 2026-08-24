package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Imposing Visage
 * {R}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has menace.
 *
 * Same one-static Aura shape as [Cooperation]: `auraTarget = Targets.Creature` is the enchant
 * restriction and `GrantKeyword`'s default `attachedCreature()` filter is "enchanted creature".
 * Menace is read by the engine's block-legality rules, so no hand-rolled `CantBeBlockedByFewerThan`
 * is needed — the printed keyword is the whole card.
 */
val ImposingVisage = card("Imposing Visage") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has menace. (It can't be blocked except by two or more creatures.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.MENACE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Phil Foglio"
        flavorText = "\"I can't believe they expect me to fight with this rabble. A Goblin in a big mask sends 'em running for cover.\"\n—Avram Garrisson, Leader of the Knights of Stromgald"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cca42b74-9b42-482b-b12a-79cafdcd087e.jpg"
    }
}
