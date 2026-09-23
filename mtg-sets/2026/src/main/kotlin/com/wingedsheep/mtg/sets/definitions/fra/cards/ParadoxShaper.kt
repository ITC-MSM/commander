package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Paradox Shaper // Omit Variables — does not enter prepared; the upkeep trigger is an
 * intervening-if (CR 603.4) on the creature not being prepared.
 */
val ParadoxShaper = card("Paradox Shaper") {
    manaCost = "{1}{U/B}"
    colorIdentity = "UB"
    typeLine = "Creature — Octopus Wizard"
    power = 1
    toughness = 3
    oracleText = "At the beginning of your upkeep, if this creature isn't prepared, it becomes prepared.\n{2}: Put target card from your graveyard on the bottom of your library."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        interveningIf = Conditions.Not(Conditions.SourceIsPrepared)
        effect = Effects.BecomePrepared(EffectTarget.Self)
        description = "At the beginning of your upkeep, if this creature isn't prepared, it becomes prepared."
    }

    activatedAbility {
        cost = Costs.Mana("{2}")
        val card = target(
            "target card from your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Any.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.Move(card, Zone.LIBRARY, ZonePlacement.Bottom)
        description = "Put target card from your graveyard on the bottom of your library."
    }

    prepare("Omit Variables") {
        manaCost = "{U/B}"
        typeLine = "Sorcery"
        oracleText = "Mill three cards. (Put the top three cards of your library into your graveyard.)"
        spell {
            effect = Patterns.Library.mill(3)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "143"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e61b9d48-0ace-4453-afe0-a1024444bac0.jpg?1788329390"
        inBooster = false
    }
}
