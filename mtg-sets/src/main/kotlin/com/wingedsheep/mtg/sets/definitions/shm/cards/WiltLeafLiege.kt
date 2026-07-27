package com.wingedsheep.mtg.sets.definitions.shm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.RedirectZoneChange
import com.wingedsheep.sdk.scripting.ZoneChangeCause

/**
 * Wilt-Leaf Liege
 * {1}{G/W}{G/W}{G/W}
 * Creature — Elf Knight
 * 4/4
 *
 * ({G/W} can be paid with either {G} or {W}.)
 * Other green creatures you control get +1/+1.
 * Other white creatures you control get +1/+1.
 * If a spell or ability an opponent controls causes you to discard this card, put it onto the
 * battlefield instead of putting it into your graveyard.
 *
 * - The two lord clauses are deliberately separate statics and stack (Scryfall ruling 2024-11-08):
 *   a green *and* white creature you control gets a total of +2/+2. Both exclude the Liege itself.
 * - The discard clause is a card-intrinsic self-replacement ([RedirectZoneChange] with
 *   `selfOnly = true`), so it functions from hand (CR 614.12) rather than requiring the Liege to be
 *   on the battlefield. [ZoneChangeCause.DiscardedByOpponentEffect] narrows it to the printed
 *   trigger condition: it does nothing when you discard to hand size in the cleanup step (a
 *   turn-based action) or to pay a cost of your own spell — only an *opponent's* spell or ability
 *   making you discard it puts it onto the battlefield.
 * - The card still counts as discarded either way (ruling 2024-11-08): the `CardsDiscardedEvent`
 *   fires before the redirect, so madness/"whenever you discard" triggers still see it.
 */
val WiltLeafLiege = card("Wilt-Leaf Liege") {
    manaCost = "{1}{G/W}{G/W}{G/W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elf Knight"
    power = 4
    toughness = 4
    oracleText = "({G/W} can be paid with either {G} or {W}.)\n" +
        "Other green creatures you control get +1/+1.\n" +
        "Other white creatures you control get +1/+1.\n" +
        "If a spell or ability an opponent controls causes you to discard this card, put it onto " +
        "the battlefield instead of putting it into your graveyard."

    // Other green creatures you control get +1/+1.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withColor(Color.GREEN).youControl(),
                excludeSelf = true
            )
        )
    }

    // Other white creatures you control get +1/+1.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Creature.withColor(Color.WHITE).youControl(),
                excludeSelf = true
            )
        )
    }

    // If a spell or ability an opponent controls causes you to discard this card, put it onto the
    // battlefield instead of putting it into your graveyard.
    replacementEffect(
        RedirectZoneChange(
            newDestination = Zone.BATTLEFIELD,
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Any,
                from = Zone.HAND,
                to = Zone.GRAVEYARD
            ),
            selfOnly = true,
            requiredCause = ZoneChangeCause.DiscardedByOpponentEffect
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "245"
        artist = "Jason Chan"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e6a2881f-e771-47d7-a39e-692054ee727f.jpg?1783942713"
    }
}
