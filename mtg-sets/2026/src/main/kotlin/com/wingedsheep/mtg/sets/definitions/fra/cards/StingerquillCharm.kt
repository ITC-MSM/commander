package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CREATED_TOKENS
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val StingerquillCharm = card("Stingerquill Charm") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Stingerquill Charm deals 3 damage to any target.\n" +
        "• Target creature gains first strike and deathtouch until end of turn.\n" +
        "• Create a 2/2 colorless Wizard Soldier creature token named Cadet. It gains haste until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Stingerquill Charm deals 3 damage to any target") {
                val t = target("any target", Targets.Any)
                effect = Effects.DealDamage(3, t)
            }
            mode("Target creature gains first strike and deathtouch until end of turn") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.Composite(
                    Effects.GrantKeyword(Keyword.FIRST_STRIKE, t),
                    Effects.GrantKeyword(Keyword.DEATHTOUCH, t)
                )
            }
            mode("Create a 2/2 colorless Wizard Soldier creature token named Cadet. It gains haste until end of turn") {
                effect = Effects.Composite(
                    Effects.CreateToken(
                        power = 2,
                        toughness = 2,
                        name = "Cadet",
                        creatureTypes = setOf("Wizard", "Soldier"),
                        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318"
                    ),
                    Effects.GrantKeyword(Keyword.HASTE, EffectTarget.PipelineTarget(CREATED_TOKENS, 0))
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "150"
        artist = "Danny Schwartz"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81733ff7-e611-43ee-bf38-6bb700676017.jpg?1789127652"
        inBooster = false
    }
}
