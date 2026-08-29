package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Galvanic Arc
 * {2}{R}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, it deals 3 damage to any target.
 * Enchanted creature has first strike.
 *
 * The enters trigger targets independently of the Aura's own enchant target — the damage can be
 * pointed anywhere, including at the enchanted creature.
 */
val GalvanicArc = card("Galvanic Arc") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, it deals 3 damage to any target.\n" +
        "Enchanted creature has first strike."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(3, t)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "126"
        artist = "Michael Sutfin"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64d816df-51e5-46f8-ad9a-68f3f656dbcd.jpg"
    }
}
