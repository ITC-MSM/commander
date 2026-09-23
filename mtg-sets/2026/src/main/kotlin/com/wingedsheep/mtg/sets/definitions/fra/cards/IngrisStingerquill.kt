package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ingris Stingerquill — the attacking creature is the damage source ("that creature deals"), so
 * lifelink/deathtouch on it apply. The {4} haste grant snapshots the creatures you control as it
 * resolves, including the Cadet just created.
 */
val IngrisStingerquill = card("Ingris Stingerquill") {
    manaCost = "{B}{R}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Elder Sphinx"
    power = 1
    toughness = 4
    oracleText = "Flying\nWhenever a creature you control attacks, that creature deals 1 damage to each opponent.\n{4}: Create a 2/2 colorless Wizard Soldier creature token named Cadet. Then creatures you control gain haste until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.attacks(filter = GameObjectFilter.Creature.youControl(), binding = TriggerBinding.ANY)
        effect = Effects.DealDamage(
            1,
            EffectTarget.PlayerRef(Player.EachOpponent),
            damageSource = EffectTarget.TriggeringEntity,
        )
        description = "Whenever a creature you control attacks, that creature deals 1 damage to each opponent."
    }

    activatedAbility {
        cost = Costs.Mana("{4}")
        effect = Effects.Composite(
            Effects.CreateToken(power = 2, toughness = 2, name = "Cadet", creatureTypes = setOf("Wizard", "Soldier"), imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318"),
            Patterns.Group.grantKeywordToAll(Keyword.HASTE, GroupFilter.AllCreaturesYouControl),
        )
        description = "Create a 2/2 colorless Wizard Soldier creature token named Cadet. Then creatures you control gain haste until end of turn."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "137"
        artist = "Chase Stone"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6471b135-33a8-4005-9a07-ebb74e0bf145.jpg?1788878212"
        inBooster = false
    }
}
