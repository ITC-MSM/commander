package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Unholy Annex // Ritual Chamber (DSK 118) — split-layout Room (CR 709.5).
 *
 * Unholy Annex {2}{B} — Enchantment — Room
 *   At the beginning of your end step, draw a card. If you control a Demon, each opponent
 *   loses 2 life and you gain 2 life. Otherwise, you lose 2 life.
 *
 * Ritual Chamber {3}{B}{B} — Enchantment — Room
 *   When you unlock this door, create a 6/6 black Demon creature token with flying.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e).
 */
val UnholyAnnexRitualChamber = card("Unholy Annex // Ritual Chamber") {
    layout = CardLayout.SPLIT
    colorIdentity = "B"

    face("Unholy Annex") {
        manaCost = "{2}{B}"
        typeLine = "Enchantment — Room"
        oracleText = "At the beginning of your end step, draw a card. If you control a Demon, " +
            "each opponent loses 2 life and you gain 2 life. Otherwise, you lose 2 life."

        triggeredAbility {
            trigger = Triggers.YourEndStep
            effect = Effects.DrawCards(1) then ConditionalEffect(
                condition = Conditions.ControlCreatureOfType(Subtype.DEMON),
                effect = Effects.LoseLife(2, EffectTarget.PlayerRef(Player.EachOpponent))
                    then Effects.GainLife(2),
                elseEffect = Effects.LoseLife(2, EffectTarget.PlayerRef(Player.You))
            )
        }
    }

    face("Ritual Chamber") {
        manaCost = "{3}{B}{B}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, create a 6/6 black Demon creature token with flying."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Effects.CreateToken(
                power = 6,
                toughness = 6,
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Demon"),
                keywords = setOf(Keyword.FLYING),
                imageUri = "https://cards.scryfall.io/normal/front/b/b/bba307eb-814c-4c87-acdf-b54c87d04f82.jpg?1726236633"
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "118"
        artist = "Matteo Bassini"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0fb4c734-c698-46f2-bc78-5f036f472e5b.jpg?1726867681"
    }
}
