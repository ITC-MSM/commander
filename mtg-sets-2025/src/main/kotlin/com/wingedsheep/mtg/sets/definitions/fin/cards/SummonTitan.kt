package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.core.Zone

/**
 * Summon: Titan
 * {3}{G}{G}
 * Enchantment Creature — Saga Giant
 * 7/7
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Mill five cards.
 * II — Return all land cards from your graveyard to the battlefield tapped.
 * III — Until end of turn, another target creature you control gains trample and gets +X/+X,
 *       where X is the number of lands you control.
 * Reach, trample
 */
val SummonTitan = card("Summon: Titan") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment Creature — Saga Giant"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Mill five cards.\n" +
        "II — Return all land cards from your graveyard to the battlefield tapped.\n" +
        "III — Until end of turn, another target creature you control gains trample and gets +X/+X, " +
        "where X is the number of lands you control.\n" +
        "Reach, trample"
    power = 7
    toughness = 7

    keywords(Keyword.REACH, Keyword.TRAMPLE)

    sagaChapter(1) { effect = Patterns.Library.mill(5) }

    sagaChapter(2) {
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.FromZone(Zone.GRAVEYARD, Player.You, GameObjectFilter.Land),
                storeAs = "graveyard_lands",
            ),
            MoveCollectionEffect(
                from = "graveyard_lands",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped),
            ),
        )
    }

    sagaChapter(3) {
        val ally = target("creature", TargetObject(filter = TargetFilter.OtherCreatureYouControl))
        val lands = DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land).count()
        effect = Effects.Composite(
            Effects.ModifyStats(lands, lands, ally),
            Effects.GrantKeyword(Keyword.TRAMPLE, ally),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "204"
        artist = "Yefim Kligerman"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5ce6ea96-7293-496d-b9c8-8ed6d6109a4d.jpg?1749123785"
    }
}
