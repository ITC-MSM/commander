package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Merfolk Trickster
 * {U}{U}
 * Creature — Merfolk Wizard
 * 2/2
 * Flash
 * When Merfolk Trickster enters the battlefield, tap target creature
 * an opponent controls. It loses all abilities until end of turn.
 */
val MerfolkTrickster = card("Merfolk Trickster") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 2
    toughness = 2
    oracleText = "Flash\nWhen Merfolk Trickster enters the battlefield, tap target creature an opponent controls. It loses all abilities until end of turn."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.Tap(t).then(Effects.RemoveAllAbilities(t))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "Jesper Ejsing"
        imageUri = "https://cards.scryfall.io/normal/front/3/5/359b2f2b-7b58-47b6-b00c-8616f981e3a3.jpg?1562733961"
    }
}
