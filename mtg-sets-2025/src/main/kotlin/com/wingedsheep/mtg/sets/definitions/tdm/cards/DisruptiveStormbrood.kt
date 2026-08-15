package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Disruptive Stormbrood // Petty Revenge — Tarkir: Dragonstorm #178
 * {4}{G} · Creature — Dragon · 3/3
 *
 * Flying
 * When this creature enters, destroy up to one target artifact or enchantment.
 *
 * Omen: Petty Revenge — {1}{B}, Sorcery — Omen
 * Destroy target creature with power 3 or less.
 *
 * (Omen, Tarkir: Dragonstorm: casting the Omen face shuffles this card into its owner's
 * library on resolution instead of putting it in the graveyard. From every zone other than
 * the stack the card is just the Dragon — see [com.wingedsheep.sdk.model.CardLayout.OMEN].)
 */
val DisruptiveStormbrood = card("Disruptive Stormbrood") {
    manaCost = "{4}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Dragon"
    power = 3
    toughness = 3
    oracleText = "Flying\nWhen this creature enters, destroy up to one target artifact or enchantment."

    keywords(Keyword.FLYING)

    // ETB: destroy up to one target artifact or enchantment.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to one target artifact or enchantment",
            TargetPermanent(filter = TargetFilter.ArtifactOrEnchantment, count = 1, optional = true)
        )
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
    }

    // Omen: Petty Revenge — Sorcery. Destroy target creature with power 3 or less.
    omen("Petty Revenge") {
        manaCost = "{1}{B}"
        typeLine = "Sorcery — Omen"
        oracleText = "Destroy target creature with power 3 or less. " +
            "(Then shuffle this card into its owner's library.)"
        spell {
            val creature = target("creature with power 3 or less", Targets.CreatureWithPowerAtMost(3))
            effect = Effects.Destroy(creature)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "178"
        artist = "Edgar Sánchez Hidalgo"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd78e8ae-e927-40e7-9580-966c5e81f53c.jpg?1743204689"
    }
}
