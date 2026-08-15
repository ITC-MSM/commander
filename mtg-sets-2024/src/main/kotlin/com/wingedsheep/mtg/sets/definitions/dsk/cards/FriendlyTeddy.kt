package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Friendly Teddy
 * {2}
 * Artifact Creature — Bear Toy
 * 2/2
 * When this creature dies, each player draws a card.
 */
val FriendlyTeddy = card("Friendly Teddy") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Bear Toy"
    oracleText = "When this creature dies, each player draws a card."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.DrawCards(1, EffectTarget.PlayerRef(Player.Each))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "247"
        artist = "Johann Bodin"
        flavorText = "Best friends are great to share secrets with—like where the matches are, or how to find a human heart."
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82b142be-4586-487a-8dd8-a55eff776458.jpg?1726286797"
    }
}
