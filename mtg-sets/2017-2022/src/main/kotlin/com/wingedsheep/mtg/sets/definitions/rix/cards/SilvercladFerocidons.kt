package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Silverclad Ferocidons
 * {5}{R}{R}
 * Creature — Dinosaur
 * 8/5
 * Enrage — Whenever this creature is dealt damage, each opponent sacrifices a permanent of their
 * choice.
 */
val SilvercladFerocidons = card("Silverclad Ferocidons") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, each opponent sacrifices a " +
        "permanent of their choice."
    power = 8
    toughness = 5

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.Sacrifice(
            GameObjectFilter.Permanent,
            1,
            EffectTarget.PlayerRef(Player.EachOpponent)
        )
        description = "Enrage — Whenever this creature is dealt damage, each opponent sacrifices " +
            "a permanent of their choice."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "115"
        artist = "Simon Dominic"
        flavorText = "\"Control them? No. I gird them in armor and let them loose.\"\n" +
            "—Yacha, Otepec huntmaster"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f0d0c8d-c057-4a44-bd1e-38e1dd175778.jpg?1783935293"
        ruling(
            "2018-01-19",
            "If lethal damage is dealt to a creature with an enrage ability, that ability " +
                "triggers. The creature with that enrage ability leaves the battlefield before " +
                "that ability resolves, so it won't be affected by the resolving ability."
        )
        ruling(
            "2018-01-19",
            "When Silverclad Ferocidons's triggered ability resolves, first the player whose " +
                "turn it is (if that player is an opponent) chooses which permanent they will " +
                "sacrifice, then each other opponent in turn order does the same, then all " +
                "chosen permanents are sacrificed at the same time. Players will know choices " +
                "made by earlier players when making their choices."
        )
        ruling(
            "2018-01-19",
            "If creatures an opponent controls are dealt lethal damage at the same time that " +
                "Silverclad Ferocidons is dealt damage, those creatures will be destroyed before " +
                "that player chooses a permanent to sacrifice."
        )
        ruling(
            "2018-01-19",
            "If multiple sources deal damage to a creature with an enrage ability at the same " +
                "time, most likely because multiple creatures blocked that creature, the enrage " +
                "ability triggers only once."
        )
    }
}
