package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Twinmaw Stormbrood // Charring Bite — Tarkir: Dragonstorm #232
 * {5}{W} · Creature — Dragon · 5/4
 *
 * Flying
 * When this creature enters, you gain 5 life.
 *
 * Omen: Charring Bite — {1}{R}, Sorcery — Omen
 * Charring Bite deals 5 damage to target creature without flying.
 *
 * (Omen, Tarkir: Dragonstorm: casting the Omen face shuffles this card into its owner's
 * library on resolution instead of putting it in the graveyard. From every zone other than
 * the stack the card is just the Dragon — see [com.wingedsheep.sdk.model.CardLayout.OMEN].)
 */
val TwinmawStormbrood = card("Twinmaw Stormbrood") {
    manaCost = "{5}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Dragon"
    power = 5
    toughness = 4
    oracleText = "Flying\nWhen this creature enters, you gain 5 life."

    keywords(Keyword.FLYING)

    // ETB: you gain 5 life.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(5)
    }

    // Omen: Charring Bite — Sorcery. Deals 5 damage to target creature without flying.
    omen("Charring Bite") {
        manaCost = "{1}{R}"
        typeLine = "Sorcery — Omen"
        oracleText = "Charring Bite deals 5 damage to target creature without flying. " +
            "(Then shuffle this card into its owner's library.)"
        spell {
            val creature = target(
                "creature without flying",
                TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING)))
            )
            effect = Effects.DealDamage(5, creature)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "232"
        artist = "Tuan Duong Chu"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/2999e3b1-6510-42b2-9429-28c07a64a44f.jpg?1743204919"
    }
}
