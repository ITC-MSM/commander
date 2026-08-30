package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Odric, Blood-Cursed (Innistrad: Crimson Vow #243)
 * {1}{R}{W} · Legendary Creature — Vampire Soldier 3/3
 *
 * When Odric enters, create X Blood tokens, where X is the number of abilities from among flying,
 * first strike, double strike, deathtouch, haste, hexproof, indestructible, lifelink, menace,
 * reach, trample, and vigilance found among creatures you control. (Count each ability only once.)
 *
 * Implementation: the printed list is a fixed twelve keywords, so X is the sum of twelve 0/1
 * terms — `1 if you control a creature with K, otherwise 0` — folded with [DynamicAmount.Add] and
 * fed to the dynamic [Effects.CreateBlood] overload, so all X tokens are created by one effect at
 * resolution (not one at a time). "Count each ability only once" falls out of the shape: each
 * keyword contributes at most one, however many creatures carry it. The keyword filter reads
 * projected state, so granted keywords count, and Odric itself is on the battlefield when the
 * trigger resolves, so keywords it has been given count too (it has none of its own).
 */
private val COUNTED_KEYWORDS = listOf(
    Keyword.FLYING,
    Keyword.FIRST_STRIKE,
    Keyword.DOUBLE_STRIKE,
    Keyword.DEATHTOUCH,
    Keyword.HASTE,
    Keyword.HEXPROOF,
    Keyword.INDESTRUCTIBLE,
    Keyword.LIFELINK,
    Keyword.MENACE,
    Keyword.REACH,
    Keyword.TRAMPLE,
    Keyword.VIGILANCE,
)

val OdricBloodCursed = card("Odric, Blood-Cursed") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Vampire Soldier"
    power = 3
    toughness = 3
    oracleText = "When Odric enters, create X Blood tokens, where X is the number of abilities from " +
        "among flying, first strike, double strike, deathtouch, haste, hexproof, indestructible, " +
        "lifelink, menace, reach, trample, and vigilance found among creatures you control. (Count " +
        "each ability only once.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateBlood(
            COUNTED_KEYWORDS
                .map<Keyword, DynamicAmount> { keyword ->
                    DynamicAmount.Conditional(
                        condition = Conditions.ControlCreatureWithKeyword(keyword),
                        ifTrue = DynamicAmount.Fixed(1),
                        ifFalse = DynamicAmount.Fixed(0),
                    )
                }
                .reduce { acc, term -> DynamicAmount.Add(acc, term) }
        )
        description = "When Odric enters, create X Blood tokens, where X is the number of abilities " +
            "from among flying, first strike, double strike, deathtouch, haste, hexproof, " +
            "indestructible, lifelink, menace, reach, trample, and vigilance found among creatures " +
            "you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "243"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81a79f5f-a65a-4b43-b58c-cdfa09cc7855.jpg?1783924793"
        ruling(
            "2021-11-19",
            "Odric, Blood-Cursed counts the number of the listed abilities found, not the number of " +
                "creatures with those abilities. If you control a single creature with both reach and " +
                "vigilance as Odric enters the battlefield, you create two Blood tokens. On the other " +
                "hand, if you control two creatures that each have flying, you create a single Blood token."
        )
        ruling(
            "2021-11-19",
            "Variants of an ability are counted as that ability and are not counted multiple times. " +
                "For example, if you control a creature with hexproof from blue and another creature " +
                "with hexproof from black, you create a single Blood token."
        )
    }
}
