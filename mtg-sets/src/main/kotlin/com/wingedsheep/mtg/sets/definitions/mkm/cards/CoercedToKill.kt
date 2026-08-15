package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ControlEnchantedPermanent
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Coerced to Kill — Murders at Karlov Manor #192
 * {3}{U}{B} · Enchantment — Aura
 *
 * Enchant creature
 * You control enchanted creature.
 * Enchanted creature has base power and toughness 1/1, has deathtouch, and is an Assassin in
 * addition to its other types.
 *
 * Dimir's take on Control Magic: you take the creature *and* shrink it to a 1/1 deathtouch body. The
 * stolen creature stops being a threat on its own stats and becomes a repeatable trade — a 1/1 that
 * kills anything it touches, which is exactly what you want from a creature you're going to throw at
 * its former controller's board.
 *
 * Four separate statics because they land in four different layers, and the layer order is what
 * makes the card work:
 *  - [ControlEnchantedPermanent] — Layer 2 (CONTROL).
 *  - [GrantSubtype] "Assassin" — Layer 4 (TYPE), *in addition to* its other types, so a stolen
 *    Sphinx is a Sphinx Assassin.
 *  - [GrantKeyword] deathtouch — Layer 6 (ABILITY).
 *  - [SetBasePowerToughnessStatic] — Layer 7b (SET_BASE_VALUES). Being a *base* P/T set, it is
 *    applied before Layer 7c modifications, so +1/+1 counters and pump effects still add on top of
 *    the 1/1 rather than being erased by it.
 *
 * [SetBasePowerToughnessStatic] and [GrantKeyword] both default their filter to the attached
 * creature, which is what an Aura wants. [GrantSubtype] does **not** — it defaults to
 * `GroupFilter.source()`, which would make the *Aura* an Assassin and leave the creature untouched,
 * so the attached-creature filter is passed explicitly.
 */
val CoercedToKill = card("Coerced to Kill") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "You control enchanted creature.\n" +
        "Enchanted creature has base power and toughness 1/1, has deathtouch, and is an Assassin " +
        "in addition to its other types."

    auraTarget = Targets.Creature

    // "You control enchanted creature." — Layer 2 (CONTROL)
    staticAbility {
        ability = ControlEnchantedPermanent
    }

    // "is an Assassin in addition to its other types" — Layer 4 (TYPE)
    staticAbility {
        ability = GrantSubtype("Assassin", GroupFilter.attachedCreature())
    }

    // "has deathtouch" — Layer 6 (ABILITY)
    staticAbility {
        ability = GrantKeyword(Keyword.DEATHTOUCH)
    }

    // "has base power and toughness 1/1" — Layer 7b (SET_BASE_VALUES)
    staticAbility {
        ability = SetBasePowerToughnessStatic(1, 1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "192"
        artist = "Justyna Dura"
        flavorText = "\"People are being brainwashed into these attacks. We must find the puppet " +
            "master.\"\n—Alquist Proft"
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2dc9f352-5076-4b5f-9815-cf47abb63d5b.jpg?1783912855"
    }
}
