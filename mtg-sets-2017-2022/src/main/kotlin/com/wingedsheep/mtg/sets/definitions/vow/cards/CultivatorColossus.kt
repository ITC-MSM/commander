package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.RepeatCondition
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cultivator Colossus — Innistrad: Crimson Vow #195
 * {4}{G}{G}{G} · Creature — Plant Beast · Mythic · star/star
 * Artist: Antonio José Manzanedo
 *
 * Trample
 * Cultivator Colossus's power and toughness are each equal to the number of lands you control.
 * When this creature enters, you may put a land card from your hand onto the battlefield tapped. If
 * you do, draw a card and repeat this process.
 *
 * The star/star body is a characteristic-defining ability keyed to your land count
 * ([dynamicStats] over [DynamicAmount.AggregateBattlefield], the Lumra shape). The ETB is a do-while
 * loop ([Effects.RepeatWhile], the Tale of Tamiyo idiom): each pass puts *up to one* land from hand
 * tapped ([Patterns.Hand.putFromHand] uses `ChooseUpTo(1)`, so choosing zero is the "you may"
 * decline) and — only if a land was actually put — draws a card. The loop's
 * [RepeatCondition.WhileCondition] re-reads *this* pass's `putting` collection, so it stops the
 * instant you decline or run out of lands. Because each pass re-gathers the hand, a land put this
 * pass can't be re-selected. Empty hand → nothing put → no draw → loop ends.
 */
val CultivatorColossus = card("Cultivator Colossus") {
    manaCost = "{4}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Beast"
    oracleText = "Trample\n" +
        "Cultivator Colossus's power and toughness are each equal to the number of lands you " +
        "control.\n" +
        "When this creature enters, you may put a land card from your hand onto the battlefield " +
        "tapped. If you do, draw a card and repeat this process."

    dynamicStats(DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Land))

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.RepeatWhile(
            body = Effects.Composite(
                // "you may put a land card from your hand onto the battlefield tapped"
                Patterns.Hand.putFromHand(GameObjectFilter.Land, count = 1, entersTapped = true),
                // "If you do, draw a card"
                ConditionalEffect(
                    condition = Conditions.CollectionContainsMatch("putting", GameObjectFilter.Land),
                    effect = Effects.DrawCards(1)
                )
            ),
            // "and repeat this process" — continue only while a land was put this pass.
            repeatCondition = RepeatCondition.WhileCondition(
                Conditions.CollectionContainsMatch("putting", GameObjectFilter.Land)
            )
        )
        description = "When this creature enters, you may put a land card from your hand onto the " +
            "battlefield tapped. If you do, draw a card and repeat this process."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "195"
        artist = "Antonio José Manzanedo"
        imageUri = "https://cards.scryfall.io/normal/front/6/2/62dffe04-c431-440d-a8da-33c74b4bb683.jpg?1783924815"
    }
}
