package com.wingedsheep.mtg.sets.definitions.scg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.GainControlByActivePlayerEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Karona, False God
 * {1}{W}{U}{B}{R}{G}
 * Legendary Creature — Avatar
 * 5/5
 *
 * Haste
 * At the beginning of each player's upkeep, that player untaps Karona and gains control of it.
 * Whenever Karona attacks, creatures of the creature type of your choice get +3/+3 until end of turn.
 */
val KaronaFalseGod = card("Karona, False God") {
    manaCost = "{1}{W}{U}{B}{R}{G}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Creature — Avatar"
    power = 5
    toughness = 5
    oracleText = "Haste\n" +
        "At the beginning of each player's upkeep, that player untaps Karona and gains control of it.\n" +
        "Whenever Karona attacks, creatures of the creature type of your choice get +3/+3 until end of turn."

    keywords(Keyword.HASTE)

    // At the beginning of each player's upkeep, that player untaps Karona and gains control of it.
    triggeredAbility {
        trigger = Triggers.EachUpkeep
        effect = Effects.Composite(
            Effects.Untap(EffectTarget.Self),
            GainControlByActivePlayerEffect(EffectTarget.Self)
        )
    }

    // Whenever Karona attacks, creatures of the creature type of your choice get +3/+3 until end of turn.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ChooseCreatureTypeModifyStats(
            powerModifier = 3,
            toughnessModifier = 3
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "138"
        artist = "Matthew D. Wilson"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de53d083-251e-42a4-9e2e-c2978c80615b.jpg?1562535715"
    }
}
