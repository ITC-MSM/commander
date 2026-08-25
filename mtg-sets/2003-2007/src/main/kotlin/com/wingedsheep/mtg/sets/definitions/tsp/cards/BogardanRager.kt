package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bogardan Rager
 * {5}{R}
 * Creature — Elemental
 * 3/4
 * Flash (You may cast this spell any time you could cast an instant.)
 * When this creature enters, target creature gets +4/+0 until end of turn.
 *
 * Flash plus a pump-on-entry is the combat trick half of the card: cast it during combat and the
 * entry trigger can push an already-declared attacker or blocker over the top. The trigger targets
 * any creature, the Rager itself included.
 */
val BogardanRager = card("Bogardan Rager") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 4
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "When this creature enters, target creature gets +4/+0 until end of turn."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(4, 0, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Clint Langley"
        flavorText = "In the erupting heart of Bogardan, it's hard to tell hurtling volcanic rocks from pouncing volcanic beasts."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec53d26b-ad3e-474f-a374-05fcdc00e49c.jpg"
    }
}
