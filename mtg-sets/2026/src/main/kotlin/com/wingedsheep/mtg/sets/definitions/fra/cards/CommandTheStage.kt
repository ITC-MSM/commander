package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersToCollectionEffect
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * "Each *other* Wizard token" excludes the Cadet this spell just made, so the Wizard tokens are
 * gathered before the Cadet is created.
 *
 * "Last turn" is the previous turn in the game, whoever's it was. The upkeep trigger works from
 * the graveyard, and its "if" is an intervening-if (CR 603.4), checked when it triggers and again
 * when it resolves.
 */
val CommandTheStage = card("Command the Stage") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create a 2/2 colorless Wizard Soldier creature token named Cadet, then put a +1/+1 counter on each other Wizard token you control.\n" +
        "At the beginning of each upkeep, if an opponent was dealt noncombat damage last turn, return this card from your graveyard to your hand."

    spell {
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.BattlefieldMatching(
                    GameObjectFilter.Any.token().withSubtype("Wizard"),
                    player = Player.You
                ),
                storeAs = "otherWizardTokens"
            ),
            Effects.CreateToken(
                power = 2,
                toughness = 2,
                name = "Cadet",
                creatureTypes = setOf("Wizard", "Soldier"),
                imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318"
            ),
            AddCountersToCollectionEffect("otherWizardTokens", Counters.PLUS_ONE_PLUS_ONE, 1)
        )
    }

    triggeredAbility {
        trigger = Triggers.EachUpkeep
        triggerZone = Zone.GRAVEYARD
        interveningIf = Conditions.OpponentWasDealtNoncombatDamageLastTurn
        effect = Effects.Move(EffectTarget.Self, Zone.HAND, fromZone = Zone.GRAVEYARD)
        description = "At the beginning of each upkeep, if an opponent was dealt noncombat damage last turn, " +
            "return this card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "77"
        artist = "Jarel Threat"
        flavorText = "Stingerquill admits only the fiercest hearts."
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3307da2-6dad-4ef2-9614-a7d34f38088e.jpg?1789644825"
        inBooster = false
    }
}
