package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.core.Keyword

/**
 * Callous Inspector
 * {B}
 * Creature — Human Soldier
 * 1/1
 *
 * Menace (This creature can't be blocked except by two or more creatures.)
 * When this creature dies, it deals 1 damage to you. Create a Clue token. (It's
 * an artifact with "{2}, Sacrifice this token: Draw a card.")
 */
val CallousInspector = card("Callous Inspector") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Soldier"
    power = 1
    toughness = 1
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "When this creature dies, it deals 1 damage to you. Create a Clue token. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")"

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.Composite(
            Effects.DealDamage(1, EffectTarget.Controller),
            Effects.CreateClue(),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Enishi"
        flavorText = "Protection fees can burn a hole in your pocket, but they're better than the alternative."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f22610e9-6b14-4914-bb67-bd6723bec9aa.jpg?1764120614"
    }
}
