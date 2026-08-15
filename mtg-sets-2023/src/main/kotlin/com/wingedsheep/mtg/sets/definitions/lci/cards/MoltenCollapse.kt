package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Molten Collapse
 * {B}{R}
 * Sorcery
 *
 * Choose one. If you descended this turn, you may choose both instead. (You descended if a
 * permanent card was put into your graveyard from anywhere.)
 * • Destroy target creature or planeswalker.
 * • Destroy target noncreature, nonland permanent with mana value 1 or less.
 *
 * The conditional modal count is a cast-time `dynamicChooseCount`, exactly the Flame of Anor
 * pattern: the floor stays `minChooseCount = 1` ("choose one" is mandatory), and the cap is 2
 * when you descended this turn (CR 700.11 — a permanent card was put into your graveyard from
 * anywhere), otherwise 1. Evaluated against the game state at cast time by
 * [com.wingedsheep.engine.handlers.actions.spell.CastSpellHandler].
 *
 * Mode 1's target is "creature or planeswalker" ([Targets.CreatureOrPlaneswalker]); mode 2's
 * target is a nonland permanent that is not a creature with mana value 1 or less.
 */
val MoltenCollapse = card("Molten Collapse") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Sorcery"
    oracleText = "Choose one. If you descended this turn, you may choose both instead. " +
        "(You descended if a permanent card was put into your graveyard from anywhere.)\n" +
        "• Destroy target creature or planeswalker.\n" +
        "• Destroy target noncreature, nonland permanent with mana value 1 or less."

    spell {
        modal(
            chooseCount = 2,
            minChooseCount = 1,
            dynamicChooseCount = DynamicAmount.Conditional(
                condition = Conditions.YouDescendedThisTurn(atLeast = 1),
                ifTrue = DynamicAmount.Fixed(2),
                ifFalse = DynamicAmount.Fixed(1)
            )
        ) {
            mode("Destroy target creature or planeswalker") {
                val victim = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
                effect = Effects.Destroy(victim)
            }
            mode("Destroy target noncreature, nonland permanent with mana value 1 or less") {
                val victim = target(
                    "target noncreature, nonland permanent with mana value 1 or less",
                    TargetPermanent(
                        filter = TargetFilter(
                            GameObjectFilter.NonlandPermanent.notCreature().manaValueAtMost(1)
                        )
                    )
                )
                effect = Effects.Destroy(victim)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "234"
        artist = "Kasia 'Kafis' Zielińska"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/2487d124-210b-4808-888c-cd0a78aebd90.jpg?1782694423"
    }
}
