package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction

/**
 * Steam Catapult
 * {3}{W}{W}
 * Creature — Human Soldier
 * 2/3
 *
 * {T}: Destroy target tapped creature. Activate only during your turn, before attackers are
 * declared.
 */
val SteamCatapult = card("Steam Catapult") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    oracleText = "{T}: Destroy target tapped creature. Activate only during your turn, before attackers are declared."
    power = 2
    toughness = 3

    activatedAbility {
        cost = Costs.Tap
        restrictions = listOf(
            ActivationRestriction.OnlyDuringYourTurn,
            ActivationRestriction.BeforeStep(Step.DECLARE_ATTACKERS)
        )
        val t = target("target", Targets.TappedCreature)
        effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Mark Tedin"
        flavorText = "\"You idiots! Turn it around! Turn it around!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdbf23ff-e095-4c5c-afe6-76badfc422c5.jpg"
    }
}
