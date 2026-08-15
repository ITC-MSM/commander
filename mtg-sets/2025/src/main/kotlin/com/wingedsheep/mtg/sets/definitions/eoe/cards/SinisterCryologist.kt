package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sinister Cryologist
 * {2}{U}
 * Creature — Jellyfish Wizard
 * When this creature enters, target creature an opponent controls gets -3/-0 until end of turn.
 * Warp {U} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)
 * 2/3
 */
val SinisterCryologist = card("Sinister Cryologist") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Jellyfish Wizard"
    oracleText = "When this creature enters, target creature an opponent controls gets -3/-0 until end of turn.\n" +
        "Warp {U} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val target = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.ModifyStats(-3, 0, target)
    }

    warp = "{U}"

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "75"
        artist = "Domenico Cava"
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8fbe740-05ec-4ced-bb9d-3084c8c2b631.jpg?1752946852"
    }
}
