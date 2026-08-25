package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Durable Coilbug
 * {1}{B}
 * Creature — Insect
 * 2/2
 *
 * {4}{B}: Return this card from your graveyard to your hand.
 *
 * The ability is activated from the graveyard, and the move carries a `fromZone` guard so a
 * card that has already left the graveyard in response isn't dragged back from elsewhere.
 */
val DurableCoilbug = card("Durable Coilbug") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    power = 2
    toughness = 2
    oracleText = "{4}{B}: Return this card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Mana("{4}{B}")
        effect = Effects.ReturnToHandFromGraveyard(EffectTarget.Self)
        activateFromZone = Zone.GRAVEYARD
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Milivoj Ćeran"
        flavorText = "\"I've seen them survive lava, trampling, and a moloch's digestive system. They just roll out and get on with their lives!\"\n—Gannet, Skysail zoologist"
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0cbdfb6-c029-440f-bc07-c95e03c20110.jpg"
    }
}
