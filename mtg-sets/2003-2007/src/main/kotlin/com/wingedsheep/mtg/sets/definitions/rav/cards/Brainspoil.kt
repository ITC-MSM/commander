package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.StatePredicate
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val Brainspoil = card("Brainspoil") {
    manaCost = "{3}{B}{B}"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature that isn't enchanted. It can't be regenerated.\nTransmute {1}{B}{B} ({1}{B}{B}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "B"

    spell {
        val creature = target(
            "target creature that isn't enchanted",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.copy(
                statePredicates = listOf(StatePredicate.Not(StatePredicate.IsEnchanted))
            )))
        )
        effect = Effects.Destroy(creature, noRegenerate = true)
    }
    transmute("{1}{B}{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "Tomas Giorello"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c34fa44f-274e-4914-bbd5-71193f8d2f96.jpg?1783943674"
    }
}
