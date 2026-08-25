package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grasping Thrull — Ravnica Allegiance #177
 * {3}{W}{B} · Creature — Thrull · 3 / 3
 *
 * The damage half hits every opponent at once via `EffectTarget.PlayerRef(Player.EachOpponent)`,
 * so in multiplayer it is 2 to each. The life gain is a flat 2 regardless of the opponent count —
 * the printed line is not "for each".
 */
val GraspingThrull = card("Grasping Thrull") {
    manaCost = "{3}{W}{B}"
    colorIdentity = "BW"
    typeLine = "Creature — Thrull"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "When this creature enters, it deals 2 damage to each opponent and you gain 2 life."

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(listOf(
            Effects.DealDamage(2, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(2)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "177"
        artist = "Seb McKinnon"
        flavorText = "\"Debt due! Debt due!\" The thrull's screeching makes children flinch and debtors quail. \"Debt due!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/6/56f82d97-ce50-490c-ad7f-46d70a73e454.jpg"
    }
}
