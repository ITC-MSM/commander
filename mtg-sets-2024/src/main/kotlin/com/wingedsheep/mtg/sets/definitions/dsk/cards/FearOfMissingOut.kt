package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.model.Rarity

/**
 * Fear of Missing Out
 * {1}{R}
 * Enchantment Creature — Nightmare
 * 2/3
 * When this creature enters, discard a card, then draw a card.
 * Delirium — Whenever this creature attacks for the first time each turn, if there are four or more
 * card types among cards in your graveyard, untap target creature. After this phase, there is an
 * additional combat phase.
 *
 * The Delirium attack ability is one triggered ability gated by an intervening "if" (CR 603.4 —
 * [Conditions.Delirium]). Per its rulings the untap and the extra combat phase resolve together:
 * if the target creature is illegal as the ability resolves, neither happens. The phase added is a
 * combat phase only ([Effects.AddCombatPhase]) — no additional main phase (so end-of-combat goes
 * straight into the next begin-combat).
 */
val FearOfMissingOut = card("Fear of Missing Out") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment Creature — Nightmare"
    oracleText = "When this creature enters, discard a card, then draw a card.\n" +
        "Delirium — Whenever this creature attacks for the first time each turn, if there are four " +
        "or more card types among cards in your graveyard, untap target creature. After this phase, " +
        "there is an additional combat phase."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Discard(1).then(Effects.DrawCards(1))
        description = "When this creature enters, discard a card, then draw a card."
    }

    triggeredAbility {
        trigger = Triggers.AttacksFirstTimeEachTurn
        triggerCondition = Conditions.Delirium(4)
        val t = target("creature", TargetCreature())
        effect = Effects.Composite(
            Effects.Untap(t),
            Effects.AddCombatPhase,
        )
        description = "Delirium — Whenever this creature attacks for the first time each turn, if " +
            "there are four or more card types among cards in your graveyard, untap target " +
            "creature. After this phase, there is an additional combat phase."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "136"
        artist = "John Stanko"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d48aaff-46ab-411b-9456-171d4709f951.jpg?1726286354"
    }
}
