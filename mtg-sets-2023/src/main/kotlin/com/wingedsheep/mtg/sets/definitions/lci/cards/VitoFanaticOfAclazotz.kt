package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.IncrementAbilityResolutionCountEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vito, Fanatic of Aclazotz — Lost Caverns of Ixalan #243
 * {2}{W}{B} · Legendary Creature — Vampire Demon · 4/4
 *
 * Flying
 * Whenever you sacrifice another permanent, you gain 2 life if this is the first time this ability
 * has resolved this turn. If it's the second time, each opponent loses 2 life. If it's the third
 * time, create a 4/3 white and black Vampire Demon creature token with flying.
 *
 * The "Victor template" — a single escalating triggered ability keyed to how many times *this
 * ability* has resolved this turn (cf. Victor, Valgavoth's Seneschal; Elrond, Lord of Rivendell).
 * The trigger fires per sacrificed permanent ([Triggers.YouSacrificeAnother], per-permanent /
 * [com.wingedsheep.sdk.scripting.TriggerBinding.OTHER] so the source sacrificing itself never fires
 * it). On each resolution it first bumps the source's per-turn resolution counter
 * ([IncrementAbilityResolutionCountEffect]) and then runs exactly one tier via
 * [Conditions.SourceAbilityResolvedNTimes] (== n). The increment must precede the conditionals or
 * the count is never read as 1/2/3. The 4th+ resolution this turn does nothing.
 */
val VitoFanaticOfAclazotz = card("Vito, Fanatic of Aclazotz") {
    manaCost = "{2}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Vampire Demon"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever you sacrifice another permanent, you gain 2 life if this is the first time this " +
        "ability has resolved this turn. If it's the second time, each opponent loses 2 life. If " +
        "it's the third time, create a 4/3 white and black Vampire Demon creature token with flying."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouSacrificeAnother(GameObjectFilter.Permanent)
        effect = Effects.Composite(
            IncrementAbilityResolutionCountEffect,
            // 1st time — you gain 2 life.
            ConditionalEffect(
                condition = Conditions.SourceAbilityResolvedNTimes(1),
                effect = Effects.GainLife(2),
            ),
            // 2nd time — each opponent loses 2 life.
            ConditionalEffect(
                condition = Conditions.SourceAbilityResolvedNTimes(2),
                effect = Effects.LoseLife(2, EffectTarget.PlayerRef(Player.EachOpponent)),
            ),
            // 3rd time — create a 4/3 white and black Vampire Demon creature token with flying.
            ConditionalEffect(
                condition = Conditions.SourceAbilityResolvedNTimes(3),
                effect = Effects.CreateToken(
                    power = 4,
                    toughness = 3,
                    colors = setOf(Color.WHITE, Color.BLACK),
                    creatureTypes = setOf("Vampire", "Demon"),
                    keywords = setOf(Keyword.FLYING),
                    imageUri = "https://cards.scryfall.io/normal/front/3/0/3005eb0a-5c96-4a07-a6b9-a907d1095cdf.jpg?1783913605",
                ),
            ),
        )
        description = "Whenever you sacrifice another permanent, you gain 2 life if this is the " +
            "first time this ability has resolved this turn. If it's the second time, each opponent " +
            "loses 2 life. If it's the third time, create a 4/3 white and black Vampire Demon " +
            "creature token with flying."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "243"
        artist = "Marta Nael"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c4fd9047-df91-4d82-be00-c623acae0f01.jpg?1782694417"
    }
}
