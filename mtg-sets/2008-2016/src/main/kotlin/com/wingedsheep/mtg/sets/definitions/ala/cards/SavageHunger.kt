package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Savage Hunger
 * {2}{G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +1/+0 and has trample.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * "Enchant creature" is the `auraTarget`, without which the Aura would have nothing to attach to.
 * The two granted characteristics are two statics whose `filter` defaults to
 * `GroupFilter.attachedCreature()` — [ModifyStats] for the pump and [GrantKeyword] for trample —
 * so each applies in its own layer to whatever the Aura is currently on. Cycling is the
 * [KeywordAbility.cycling] keyword ability, never a plain `Keyword` entry.
 */
val SavageHunger = card("Savage Hunger") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+0 and has trample.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 0)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Trevor Claxton"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/0367fac8-6990-4544-ac7d-ed363b55a9cf.jpg"
    }
}
