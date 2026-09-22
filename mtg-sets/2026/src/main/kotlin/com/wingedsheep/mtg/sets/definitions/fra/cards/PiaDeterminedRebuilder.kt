package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

val PiaDeterminedRebuilder = card("Pia, Determined Rebuilder") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Artificer"
    power = 2
    toughness = 2
    oracleText = "When Pia enters, create a 1/1 colorless Thopter artifact creature token with flying.\n{5}{R}: Target creature gets +X/+0 until end of turn, where X is the number of artifacts you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(power = 1, toughness = 1, creatureTypes = setOf("Thopter"), artifactToken = true, keywords = setOf(Keyword.FLYING), imageUri = "https://cards.scryfall.io/normal/front/b/f/bfd6132f-c96b-4ce0-ac4d-c46356afc767.jpg?1789735223")
    }
    activatedAbility {
        cost = Costs.Mana("{5}{R}")
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Artifact).count(),
            DynamicAmount.Fixed(0), creature
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "250"
        artist = "Andreas Zafiratos"
        flavorText = "\"We've kept one foot in the past for too long. Now we walk into a better future!\""
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd3faaf4-45ca-4714-8dbe-37102ec131cf.jpg?1789385851"
        inBooster = false
    }
}
