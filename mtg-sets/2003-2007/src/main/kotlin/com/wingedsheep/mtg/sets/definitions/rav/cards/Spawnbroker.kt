package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.EntityReference

val Spawnbroker = card("Spawnbroker") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, you may exchange control of target creature you control and target creature with power less than or equal to that creature's power an opponent controls."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        description = "You may exchange control of the two targeted creatures."
        val yours = target("creature you control", Targets.CreatureYouControl)
        val theirs = target(
            "opponent's creature with power no greater than your chosen creature",
            TargetCreature(filter = Targets.Unified.creature {
                opponentControls().powerAtMostEntity(EntityReference.Target(0))
            })
        )
        effect = Effects.ExchangeControl(yours, theirs)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "65"
        artist = "Wayne England"
        flavorText = "\"The trick isn't setting up the bad trade. It's making each side think it got the better deal.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/e/ce1e4aa6-b06d-4b87-9912-a4c403e8d7c6.jpg?1783943680"
        ruling("2005-10-01", "If either target becomes illegal (say, if a creature’s power has changed such that the creature you control now has less power than the other creature), then the exchange won’t happen.")
    }
}
