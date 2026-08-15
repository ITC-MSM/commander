package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Triceraton Commander
 * {X}{X}{W}{W}
 * Creature — Dinosaur Soldier
 * 2/2
 *
 * Flying
 * Whenever this creature attacks, Dinosaurs you control other than
 * this creature get +1/+1 and gain flying until end of turn.
 * When this creature enters, create X 2/2 white Dinosaur Soldier
 * creature tokens.
 */
val TriceratonCommander = card("Triceraton Commander") {
    manaCost = "{X}{X}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur Soldier"
    oracleText = "Flying\nWhenever this creature attacks, Dinosaurs you control other than this creature get +1/+1 and gain flying until end of turn.\nWhen this creature enters, create X 2/2 white Dinosaur Soldier creature tokens."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = CreateTokenEffect(
            count = DynamicAmount.XValue,
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Dinosaur", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/f/8/f8845523-62d5-451c-aa20-780c64bb44b3.jpg?1771590377"
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(
                GameObjectFilter.Creature.withSubtype("Dinosaur").youControl(),
                excludeSelf = true
            ),
            effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
                .then(Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self, Duration.EndOfTurn))
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "25"
        artist = "Nathaniel Himawan"
        flavorText = "\"For the glory of the Republic!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/4/6445b690-71ce-4371-ae9c-bac0e70dda81.jpg?1769005579"
    }
}
