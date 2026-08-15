package com.wingedsheep.mtg.sets.definitions.khc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount


/**
 * Lathril, Blade of the Elves
 * {2}{B}{G}
 * Legendary Creature — Elf Noble
 * 2/3
 * Menace (This creature can't be blocked except by two or more creatures.)
 * Whenever Lathril deals combat damage to a player, create that many 1/1 green Elf Warrior creature tokens.
 * {T}, Tap ten untapped Elves you control: Each opponent loses 10 life and you gain 10 life.
 *
 * "That many" reads the trigger's damage payload, so a pumped Lathril makes the right number of
 * tokens. The tap-ten cost excludes the source (`excludeSelf = true`): per the KHC ruling Lathril
 * doesn't count as one of the ten — she is already being tapped by the {T} half of the same cost.
 * The life swing is a fixed 10 each way, not a drain — the controller gains 10 regardless of how
 * much life the opponents actually lost.
 */
val LathrilBladeOfTheElves = card("Lathril, Blade of the Elves") {
    manaCost = "{2}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Legendary Creature — Elf Noble"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\nWhenever Lathril deals combat damage to a player, create that many 1/1 green Elf Warrior creature tokens.\n{T}, Tap ten untapped Elves you control: Each opponent loses 10 life and you gain 10 life."
    power = 2
    toughness = 3
    keywords(Keyword.MENACE)
    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.CreateToken(
            count = DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elf", "Warrior"),
            imageUri = "https://cards.scryfall.io/normal/front/1/1/118d0655-5719-4512-8bc1-fe759669811b.jpg?1783928078"
        )
        description = "Whenever Lathril deals combat damage to a player, create that many 1/1 " +
            "green Elf Warrior creature tokens."
    }
    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.TapPermanents(
                count = 10,
                filter = GameObjectFilter.Creature.withSubtype("Elf"),
                excludeSelf = true
            )
        )
        effect = Effects.Composite(
            Effects.LoseLife(10, EffectTarget.PlayerRef(Player.EachOpponent)),
            GainLifeEffect(10)
        )
        description = "Each opponent loses 10 life and you gain 10 life."
    }
    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "1"
        artist = "Caroline Gariba"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/547888c3-a9a6-4413-b29a-6bcd8a9279bf.jpg?1783928341"

        ruling(
            "2021-02-05",
            "You can tap any ten untapped Elves you control, including ones you haven't controlled " +
                "continuously since the beginning of your most recent turn, to pay that part of the cost " +
                "of Lathril's activated ability. You must have controlled Lathril continuously since the " +
                "beginning of your most recent turn, however. Lathril doesn't count as one of the ten."
        )
    }
}
