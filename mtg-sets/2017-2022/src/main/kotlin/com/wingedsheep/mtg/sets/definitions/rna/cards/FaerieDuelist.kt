package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Faerie Duelist — Ravnica Allegiance #39
 * {1}{U} · Creature — Faerie Rogue · 1 / 2
 *
 * Flash plus flying makes it an ambush blocker; the enters trigger shrinks an attacker's
 * power. -2/-0 is a stat modification, so a creature already at 0 power simply stays there.
 */
val FaerieDuelist = card("Faerie Duelist") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Rogue"
    power = 1
    toughness = 2
    oracleText = "Flash\n" +
        "Flying\n" +
        "When this creature enters, target creature an opponent controls gets -2/-0 until end of turn."

    keywords(Keyword.FLASH, Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("target", Targets.CreatureOpponentControls)
        effect = Effects.ModifyStats(-2, 0, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Yongjae Choi"
        flavorText = "Faeries are easily offended and quick to exact a quirky revenge."
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a7da9c9b-aeef-4f48-bc8f-39425841cc8c.jpg"
    }
}
