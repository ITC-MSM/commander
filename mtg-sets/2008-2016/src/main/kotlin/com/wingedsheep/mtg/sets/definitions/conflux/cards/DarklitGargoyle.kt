package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Darklit Gargoyle
 * {1}{W}
 * Artifact Creature — Gargoyle
 * 1 / 2
 * Flying
 * {B}: This creature gets +2/-1 until end of turn.
 *
 * A bare [Costs.Mana] activation — no tap, no target — whose [Effects.ModifyStats] points at
 * [EffectTarget.Self]. The default duration is already `Duration.EndOfTurn`, so the printed
 * "until end of turn" adds no argument.
 */
val DarklitGargoyle = card("Darklit Gargoyle") {
    manaCost = "{1}{W}"
    colorIdentity = "BW"
    typeLine = "Artifact Creature — Gargoyle"
    power = 1
    toughness = 2
    oracleText = "Flying\n" +
        "{B}: This creature gets +2/-1 until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(2, -1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Howard Lyon"
        flavorText = "It shines in the darkness of its master's ambitions."
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e7162e5-8c56-457e-91eb-b8ae4d1b6adb.jpg"
    }
}
