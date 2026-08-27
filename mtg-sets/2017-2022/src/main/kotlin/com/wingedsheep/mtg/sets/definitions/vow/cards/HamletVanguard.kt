package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Hamlet Vanguard
 * {2}{G}
 * Creature — Human Warrior
 * 1/1
 *
 * Ward {2}
 * This creature enters with two +1/+1 counters on it for each other nontoken Human you control.
 *
 * [EntersWithDynamicCounters] over a [DynamicAmount.Multiply] of 2: the counted set is "other
 * nontoken Human you control", and the printed "two … for each" is the multiplier rather than a
 * second count.
 *
 * "Other" is `AggregateBattlefield.excludeSelf` rather than a filter predicate — the Vanguard is
 * itself a Human, and the SDK puts the word on the tally (Giada, Font of Hope reads the same way).
 * The ETB replacement evaluates the count with `sourceId` set to the entering permanent, so
 * `excludeSelf` drops exactly the Vanguard while `Player.You` still names its controller.
 * `nontoken()` keeps a board of Human tokens from paying the card twice, and the noun is
 * `Permanent` rather than `Creature` because "Human" in a printed count names a permanent with that
 * subtype, not a type line the card also asserts.
 *
 * `otherOnly` stays false, which is what scopes the replacement to the Vanguard's own entry: the
 * global sweep in `EntersWithReplacements` only considers a dynamic-counters effect when
 * `otherOnly` is set, so the default form applies to the permanent that carries it and to nothing
 * else.
 */
val HamletVanguard = card("Hamlet Vanguard") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Warrior"
    power = 1
    toughness = 1
    oracleText = "Ward {2} (Whenever this creature becomes the target of a spell or ability an " +
        "opponent controls, counter it unless that player pays {2}.)\n" +
        "This creature enters with two +1/+1 counters on it for each other nontoken Human you control."

    keywordAbility(KeywordAbility.ward("{2}"))

    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmount.Multiply(
                DynamicAmount.AggregateBattlefield(
                    player = Player.You,
                    filter = GameObjectFilter.Permanent.withSubtype(Subtype.HUMAN).nontoken(),
                    excludeSelf = true,
                ),
                multiplier = 2,
            )
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "201"
        artist = "PINDURSKI"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f31e108b-8cb5-4f30-b68b-274aa7ac2a81.jpg?1783924812"
    }
}
