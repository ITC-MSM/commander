package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lorehold Command — Strixhaven: School of Mages #199 (canonical printing)
 * {3}{R}{W} · Instant
 *
 * Choose two —
 * • Create a 3/2 red and white Spirit creature token.
 * • Creatures you control get +1/+0 and gain indestructible and haste until end of turn.
 * • Lorehold Command deals 3 damage to any target. Target player gains 3 life.
 * • Sacrifice a permanent, then draw two cards.
 *
 * A `modal(chooseCount = 2)` spell with per-mode targets. The pump mode is the Overrun shape — one
 * [Effects.ForEachInGroup] over creatures you control carrying a single composite of the stat bump
 * and both keyword grants, so the group is gathered exactly once. The damage mode binds two targets
 * (any target, then a player); the sacrifice mode is the bare imperative [Effects.SacrificeOwn]
 * followed by the draw.
 */
val LoreholdCommand = card("Lorehold Command") {
    manaCost = "{3}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Instant"
    oracleText =
        "Choose two —\n" +
        "• Create a 3/2 red and white Spirit creature token.\n" +
        "• Creatures you control get +1/+0 and gain indestructible and haste until end of turn.\n" +
        "• Lorehold Command deals 3 damage to any target. Target player gains 3 life.\n" +
        "• Sacrifice a permanent, then draw two cards."

    spell {
        modal(chooseCount = 2) {
            mode("Create a 3/2 red and white Spirit creature token") {
                effect = Effects.CreateToken(
                    power = 3,
                    toughness = 2,
                    colors = setOf(Color.RED, Color.WHITE),
                    creatureTypes = setOf("Spirit")
                )
            }
            mode("Creatures you control get +1/+0 and gain indestructible and haste until end of turn") {
                effect = Effects.ForEachInGroup(
                    GroupFilter.AllCreaturesYouControl,
                    Effects.Composite(
                        Effects.ModifyStats(1, 0, EffectTarget.Self),
                        Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, EffectTarget.Self),
                        Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
                    )
                )
            }
            mode("Lorehold Command deals 3 damage to any target. Target player gains 3 life") {
                val victim = target("any target", Targets.Any)
                val player = target("target player", Targets.Player)
                effect = Effects.DealDamage(3, victim) then Effects.GainLife(3, player)
            }
            mode("Sacrifice a permanent, then draw two cards") {
                effect = Effects.SacrificeOwn(GameObjectFilter.Permanent) then Effects.DrawCards(2)
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "199"
        artist = "Jason Rainville"
        imageUri = "https://cards.scryfall.io/normal/front/e/4/e4f0885f-1049-4a19-853d-f4e6d4bec29e.jpg?1783927309"
    }
}
