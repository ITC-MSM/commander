package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Spider-Mobile
 * {3}
 * Artifact — Vehicle, 3/3
 * Trample
 * Whenever this Vehicle attacks or blocks, it gets +1/+1 until end of turn for each Spider you control.
 * Crew 2
 */
val SpiderMobile = card("Spider-Mobile") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    oracleText = "Trample\nWhenever this Vehicle attacks or blocks, it gets +1/+1 until end of turn for each Spider you control.\nCrew 2"
    power = 3
    toughness = 3
    keywords(Keyword.TRAMPLE)
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ModifyStats(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.withSubtype("Spider")),
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.withSubtype("Spider")),
            EffectTarget.Self
        )
    }
    triggeredAbility {
        trigger = Triggers.Blocks
        effect = Effects.ModifyStats(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.withSubtype("Spider")),
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.withSubtype("Spider")),
            EffectTarget.Self
        )
    }
    keywordAbility(KeywordAbility.crew(2))
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Bastien Grivet"
        flavorText = "With Johnny Storm's help, Spider-Man took the all-terrain vehicle to the next level."
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f12664c0-d7cd-4acb-87db-cfa3c85f32a9.jpg?1757378087"
    }
}
