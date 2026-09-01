package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Brinebarrow Intruder
 * {U}
 * Creature — Human Rogue
 * 1/2
 * Flash
 * When this creature enters, target creature an opponent controls gets -2/-0 until end of turn.
 *
 * Flash plus a targeted enters trigger: cast during combat, the -2/-0 shrinks an attacker or
 * blocker before damage. The -0 toughness half is written out so the modifier reads as printed.
 */
val BrinebarrowIntruder = card("Brinebarrow Intruder") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Rogue"
    oracleText = "Flash\n" +
        "When this creature enters, target creature an opponent controls gets -2/-0 until end of turn."
    power = 1
    toughness = 2

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.ModifyStats(-2, 0, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Scott Murphy"
        flavorText = "Locked in the ice of Karfell, the treasures of ancient nobles await in ruins guarded by the dead."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/89960a74-9e17-4e30-874a-4286dd4c917d.jpg"
    }
}
