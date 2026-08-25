package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Slimebind — Ravnica Allegiance #54
 * {1}{U} · Enchantment — Aura
 *
 * `auraTarget` carries the "Enchant creature" line — both the cast-time target and the standing
 * attachment restriction (CR 303.4). The -4/-0 is a *filterless* [ModifyStats] static: with no
 * filter it applies to whatever the Aura is attached to and follows the attachment automatically.
 * Flash makes it a combat trick that blanks an attacker's damage without killing it.
 */
val Slimebind = card("Slimebind") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Enchant creature\n" +
        "Enchanted creature gets -4/-0."

    keywords(Keyword.FLASH)

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(-4, 0)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Mark Behm"
        flavorText = "\"Relax. It's quite harmless. And it will dissolve completely in a month or two.\"\n" +
        "—Navona, Simic field tester"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6263410-20c5-43b4-8183-3c02c10d07fb.jpg"
    }
}
