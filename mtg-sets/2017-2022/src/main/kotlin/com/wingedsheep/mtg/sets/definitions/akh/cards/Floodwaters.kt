package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Floodwaters
 * {4}{U}{U}
 * Sorcery
 * Return up to two target creatures to their owners' hands.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "Up to two target creatures" is one requirement with `count = 2, optional = true`; the body runs
 * once per chosen target via [ForEachTargetEffect], so [EffectTarget.ContextTarget] index 0 is the
 * creature of the current iteration rather than the first-chosen one.
 */
val Floodwaters = card("Floodwaters") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return up to two target creatures to their owners' hands.\n" +
            "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        target("target", Targets.UpToCreatures(2))
        effect = ForEachTargetEffect(
            effects = listOf(Effects.ReturnToHand(EffectTarget.ContextTarget(0)))
        )
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "53"
        artist = "Jung Park"
        flavorText = "\"It usually appears placid, but don't be fooled. The Luxa River is a snake, and it can swallow you whole.\"\n—Neponem, vizier of Kefnet"
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a211df0-fe9e-4d2c-9e0e-c7be50e6b906.jpg?1783936522"
    }
}
