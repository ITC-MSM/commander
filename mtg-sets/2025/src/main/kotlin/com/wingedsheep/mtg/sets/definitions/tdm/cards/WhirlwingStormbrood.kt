package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantFlashToSpellType
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Whirlwing Stormbrood // Dynamic Soar — Tarkir: Dragonstorm #234
 * {4}{U} · Creature — Dragon · 4/3
 *
 * Flash
 * Flying
 * You may cast sorcery spells and Dragon spells as though they had flash.
 *
 * Omen: Dynamic Soar — {2}{G}, Sorcery — Omen
 * Put three +1/+1 counters on target creature you control.
 *
 * (Omen, Tarkir: Dragonstorm: casting the Omen face shuffles this card into its owner's
 * library on resolution instead of putting it in the graveyard. From every zone other than
 * the stack the card is just the Dragon — see [com.wingedsheep.sdk.model.CardLayout.OMEN].)
 */
val WhirlwingStormbrood = card("Whirlwing Stormbrood") {
    manaCost = "{4}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 3
    oracleText = "Flash\nFlying\nYou may cast sorcery spells and Dragon spells as though they had flash."

    keywords(Keyword.FLASH, Keyword.FLYING)

    // You may cast sorcery spells and Dragon spells as though they had flash.
    staticAbility {
        ability = GrantFlashToSpellType(
            filter = GameObjectFilter.Sorcery or GameObjectFilter.Any.withSubtype(Subtype.DRAGON),
            controllerOnly = true
        )
    }

    // Omen: Dynamic Soar — Sorcery. Put three +1/+1 counters on target creature you control.
    omen("Dynamic Soar") {
        manaCost = "{2}{G}"
        typeLine = "Sorcery — Omen"
        oracleText = "Put three +1/+1 counters on target creature you control. " +
            "(Then shuffle this card into its owner's library.)"
        spell {
            val creature = target("creature you control", Targets.CreatureYouControl)
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 3, creature)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Fajareka Setiawan"
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56a25eb1-bdb8-4f86-8d9a-3055ad1b2a13.jpg?1743204927"
    }
}
