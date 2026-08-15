package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Iridescent Tiger — Tarkir: Dragonstorm #109
 * {4}{R} · Creature — Cat · 3/4
 *
 * When this creature enters, if you cast it, add {W}{U}{B}{R}{G}.
 *
 * The "if you cast it" clause is an intervening-if on the enters trigger, modeled as
 * [Conditions.WasCast] on the EntersBattlefield trigger — so the mana is only produced
 * when the creature was cast (not when put onto the battlefield by another effect). The
 * five-color mana is a composite of five single-color [Effects.AddMana] additions.
 */
val IridescentTiger = card("Iridescent Tiger") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Cat"
    power = 3
    toughness = 4
    oracleText = "When this creature enters, if you cast it, add {W}{U}{B}{R}{G}."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.WasCast
        effect = Effects.Composite(
            Effects.AddMana(Color.WHITE),
            Effects.AddMana(Color.BLUE),
            Effects.AddMana(Color.BLACK),
            Effects.AddMana(Color.RED),
            Effects.AddMana(Color.GREEN)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Fajareka Setiawan"
        flavorText = "The power of the dragonstorms transformed not only the land but many of the creatures within it."
        imageUri = "https://cards.scryfall.io/normal/front/e/3/e3abbc8b-2bf8-478e-a541-f8019d150054.jpg?1743213469"
    }
}
