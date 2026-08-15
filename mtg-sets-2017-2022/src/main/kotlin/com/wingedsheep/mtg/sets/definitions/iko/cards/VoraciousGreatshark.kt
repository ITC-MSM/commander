package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Voracious Greatshark
 * {3}{U}{U}
 * Creature — Shark
 * 5/4
 * Flash
 * When this creature enters, counter target artifact or creature spell.
 *
 * Flash is what makes the enters-trigger usable as a counterspell: the Shark is cast in response
 * to the spell it will counter, resolves first, and its trigger then goes on the stack above the
 * still-unresolved artifact/creature spell. The trigger targets on the stack
 * ([Zone.STACK]); with no legal artifact or creature spell it simply isn't put on the stack.
 */
val VoraciousGreatshark = card("Voracious Greatshark") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Shark"
    power = 5
    toughness = 4
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "When this creature enters, counter target artifact or creature spell."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "target artifact or creature spell",
            TargetSpell(filter = TargetFilter(GameObjectFilter.CreatureOrArtifact, zone = Zone.STACK))
        )
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "70"
        artist = "Mathias Kollros"
        flavorText = "There is no boat big enough."
        imageUri = "https://cards.scryfall.io/normal/front/1/4/1400155f-8911-45fd-aab2-998c8a28292c.jpg?1783931068"
    }
}
