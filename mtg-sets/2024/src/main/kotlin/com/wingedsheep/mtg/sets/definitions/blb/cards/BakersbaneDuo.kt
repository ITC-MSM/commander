package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bakersbane Duo
 * {1}{G}
 * Creature — Squirrel Raccoon
 * 2/2
 *
 * When this creature enters, create a Food token.
 * Whenever you expend 4, this creature gets +1/+1 until end of turn.
 * (You expend 4 as you spend your fourth total mana to cast spells during a turn.)
 */
val BakersbaneDuo = card("Bakersbane Duo") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Squirrel Raccoon"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create a Food token.\nWhenever you expend 4, this creature gets +1/+1 until end of turn. (You expend 4 as you spend your fourth total mana to cast spells during a turn.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
    }

    triggeredAbility {
        trigger = Triggers.Expend(4)
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "163"
        artist = "Raluca Marinescu"
        flavorText = "\"I'll take the crust!\" chittered the squirrel. \"And I'll take the filling!\" drooled the raccoon."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/5309354f-1ff4-4fa9-9141-01ea2f7588ab.jpg?1721426764"

        ruling("2024-07-26", "Abilities that trigger whenever you \"expend N\" only trigger when you reach that specific amount of mana spent on casting spells that turn. This can only happen once per turn.")
    }
}
