package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.RepeatCondition
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.dsl.Triggers

/**
 * Sin, Spira's Punishment
 * {4}{B}{G}{U}
 * Legendary Creature — Leviathan Avatar
 * 7/7
 * Flying
 * Whenever Sin enters or attacks, exile a permanent card from your graveyard at random, then
 *   create a tapped token that's a copy of that card. If the exiled card is a land card, repeat
 *   this process.
 *
 * "Enters or attacks" is the Gilgamesh, Master-at-Arms / Frodo, Determined Hero shape: the engine
 * has no combined enters-or-attacks trigger, so this is two sibling triggered abilities sharing one
 * [sinExileAndCopyLoop] body.
 *
 * The body is a do-while loop ([Effects.RepeatWhile]). Each pass:
 *   1. gathers the permanent cards in your graveyard (`sinGraveyardPool`),
 *   2. picks one at random ([PipelineBuilder.chooseRandom]),
 *   3. moves it to exile, recording it in `sinExiled` ([PipelineBuilder.moveTracked]),
 *   4. creates a tapped token that's a copy of that exiled card
 *      ([Effects.CreateTokenCopyOfTarget] over `sinExiled[0]`).
 * The loop's [RepeatCondition.WhileCondition] re-runs the body while `sinExiled` (this pass's exiled
 * card) is a land — mirroring The Tale of Tamiyo's "repeat this process" over the pass's own
 * collection. The RepeatWhile executor evaluates the condition against the body's own pipeline
 * outputs, so `sinExiled` refers to the card exiled *this* iteration.
 *
 * Empty/no-permanent graveyard is handled gracefully: the gather is empty, so the random select and
 * move are no-ops, the token-copy target resolves to nothing (no token), and the land check is false
 * (loop ends). Because a later pass re-gathers from the graveyard, an already-exiled card can't be
 * repicked.
 *
 * Per the card's rulings, the token copies only the exiled card's printed characteristics (which
 * [Effects.CreateTokenCopyOfTarget] reads from the card), {X} in a copied cost is 0, and any
 * "when this enters" abilities on the copied card trigger for the token as normal.
 */
val SinSpirasPunishment = card("Sin, Spira's Punishment") {
    manaCost = "{4}{B}{G}{U}"
    colorIdentity = "BGU"
    typeLine = "Legendary Creature — Leviathan Avatar"
    power = 7
    toughness = 7
    oracleText = "Flying\n" +
        "Whenever Sin enters or attacks, exile a permanent card from your graveyard at random, then " +
        "create a tapped token that's a copy of that card. If the exiled card is a land card, repeat " +
        "this process."

    keywords(Keyword.FLYING)

    // "Whenever Sin enters …"
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = sinExileAndCopyLoop()
    }

    // "… or attacks"
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = sinExileAndCopyLoop()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "242"
        artist = "John Tedrick"
        imageUri = "https://cards.scryfall.io/normal/front/6/5/659be746-bd31-4a70-8cec-7798da78b0b5.jpg?1782686408"

        ruling(
            "2025-06-06",
            "If the copied card has {X} in its mana cost, X is 0."
        )
        ruling(
            "2025-06-06",
            "The token copies exactly what was printed on the original card and nothing else. It " +
                "doesn't copy any information about the object the card was before it was put into " +
                "your graveyard."
        )
        ruling(
            "2025-06-06",
            "If a card copied by the token had any \"when [this permanent] enters\" abilities, the " +
                "token also has those abilities, and they'll trigger when it's created."
        )
    }
}

/** The shared "exile a random permanent card, copy it tapped, repeat if it was a land" loop. */
private fun sinExileAndCopyLoop(): Effect = Effects.RepeatWhile(
    body = Effects.Pipeline {
        val pool = gather(
            CardSource.FromZone(
                zone = Zone.GRAVEYARD,
                player = Player.You,
                filter = GameObjectFilter.Permanent
            ),
            name = "sinGraveyardPool"
        )
        val chosen = chooseRandom(1, from = pool, name = "sinChosen")
        val exiled = moveTracked(
            chosen,
            CardDestination.ToZone(Zone.EXILE, Player.You),
            name = "sinExiled"
        )
        run(
            Effects.CreateTokenCopyOfTarget(
                target = EffectTarget.PipelineTarget(exiled.key, 0),
                tapped = true
            )
        )
    },
    repeatCondition = RepeatCondition.WhileCondition(
        Conditions.CollectionContainsMatch("sinExiled", GameObjectFilter.Land)
    ),
)
