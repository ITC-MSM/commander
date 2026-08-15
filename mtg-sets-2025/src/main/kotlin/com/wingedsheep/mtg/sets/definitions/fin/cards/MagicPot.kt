package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject


/**
 * Magic Pot
 * {3}
 * Artifact Creature — Goblin Construct
 * 1/4
 * When this creature dies, create a Treasure token. (It's an artifact with "{T}, Sacrifice this token: Add one mana of any color.")
 * {2}, {T}: Exile target card from a graveyard.
 */
val MagicPot = card("Magic Pot") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Goblin Construct"
    oracleText = "When this creature dies, create a Treasure token. (It's an artifact with \"{T}, Sacrifice this token: Add one mana of any color.\")\n{2}, {T}: Exile target card from a graveyard."
    power = 1
    toughness = 4
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateTreasure(1)
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val t = target("target", TargetObject(filter = TargetFilter.CardInGraveyard))
        effect = Effects.Move(t, Zone.EXILE)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "263"
        artist = "David Astruga"
        flavorText = "\"Gimme elixir!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57d07ca0-5618-4a90-a605-ca14a193ce3b.jpg"
    }
}
