package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stingerquill Voxmancer // Vicious Verse — does not enter prepared. Its upkeep trigger is an
 * intervening-if (CR 603.4) on the creature not being prepared.
 */
val StingerquillVoxmancer = card("Stingerquill Voxmancer") {
    manaCost = "{B/R}"
    colorIdentity = "BR"
    typeLine = "Creature — Goblin Sorcerer"
    power = 1
    toughness = 2
    oracleText = "At the beginning of your upkeep, if this creature isn't prepared, it becomes prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        interveningIf = Conditions.Not(Conditions.SourceIsPrepared)
        effect = Effects.BecomePrepared(EffectTarget.Self)
        description = "At the beginning of your upkeep, if this creature isn't prepared, it becomes prepared."
    }

    prepare("Vicious Verse") {
        manaCost = "{B/R}"
        typeLine = "Sorcery"
        oracleText = "Vicious Verse deals 1 damage to target opponent."
        spell {
            val opponent = target("target opponent", Targets.Opponent)
            effect = Effects.DealDamage(1, opponent)
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Igor Grechanyi"
        imageUri = "https://cards.scryfall.io/normal/front/8/4/84b1c268-3b8a-41b6-92e3-a2ce0cc3d738.jpg?1788329418"
        inBooster = false
    }
}
