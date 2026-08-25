package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Easy Prey
 * {1}{B}
 * Instant
 * Destroy target creature with mana value 2 or less.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * The mana-value cap is a predicate on the target filter rather than a condition on the effect:
 * `TargetFilter.Creature.manaValueAtMost(2)` bounds the legal-target set, so an illegal creature
 * can never be chosen and CR 608.2b re-checks it at resolution for free.
 */
val EasyPrey = card("Easy Prey") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target creature with mana value 2 or less.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.manaValueAtMost(2)))
        effect = Effects.Destroy(t)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "87"
        artist = "Ekaterina Burmak"
        flavorText = "The definition of \"bite-sized treat\" depends on who you ask."
        imageUri = "https://cards.scryfall.io/normal/front/3/1/312fb6e4-1eb1-4fbb-b7a4-125829a6e96a.jpg"
    }
}
