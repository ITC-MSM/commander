package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Rinoa Heartilly
 * {3}{G}{W}
 * Legendary Creature — Human Rebel Warlock
 * 4/4
 * When Rinoa Heartilly enters, create Angelo, a legendary 1/1 green and white Dog creature token.
 * Angelo Cannon — Whenever Rinoa Heartilly attacks, another target creature you control gets +1/+1
 * until end of turn for each creature you control.
 */
val RinoaHeartilly = card("Rinoa Heartilly") {
    manaCost = "{3}{G}{W}"
    colorIdentity = "WG"
    typeLine = "Legendary Creature — Human Rebel Warlock"
    oracleText = "When Rinoa Heartilly enters, create Angelo, a legendary 1/1 green and white Dog creature token.\nAngelo Cannon — Whenever Rinoa Heartilly attacks, another target creature you control gets +1/+1 until end of turn for each creature you control."
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            count = 1,
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN, Color.WHITE),
            creatureTypes = setOf("Dog"),
            name = "Angelo",
            legendary = true,
            imageUri = "https://cards.scryfall.io/normal/front/5/4/54347961-0a41-4f62-b47e-2afa0ca07b21.jpg?1748704090"
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        val t = target("target", TargetCreature(filter = TargetFilter.OtherCreatureYouControl))
        effect = Effects.ModifyStats(
            power = DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature),
            toughness = DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Creature),
            target = t
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Francesca Resta"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba79d293-bf42-48b6-a868-5249f4beeb76.jpg?1748706666"
    }
}
