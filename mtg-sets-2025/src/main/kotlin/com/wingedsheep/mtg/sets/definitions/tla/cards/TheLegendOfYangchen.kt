package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.targets.TargetOther
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Legend of Yangchen // Avatar Yangchen (TLA #27)
 * {3}{W}{W} — Enchantment — Saga
 * //  — Legendary Creature — Avatar 4/5
 *
 * Front — The Legend of Yangchen:
 *   (As this Saga enters and after your draw step, add a lore counter.)
 *   I — Starting with you, each player chooses up to one permanent with mana value 3 or greater
 *       from among permanents your opponents control. Exile those permanents.
 *   II — You may have target opponent draw three cards. If you do, draw three cards.
 *   III — Exile this Saga, then return it to the battlefield transformed under your control.
 *
 * Back — Avatar Yangchen:
 *   Flying
 *   Whenever you cast your second spell each turn, airbend up to one other target nonland permanent.
 *   (Exile it. While it's exiled, its owner may cast it for {2} rather than its mana cost.)
 *
 * **Chapter I** — the pool is *fixed* to the Saga controller's opponents' permanents (mana value 3+)
 * for every chooser, which is the wrinkle that separates it from Destined Confrontation / Bend or
 * Break (where each player picks from their *own* permanents). A naive `ForEachPlayerEffect` would be
 * wrong here: that facade rebinds the per-iteration controller, so `Player.You` / `opponentControls()`
 * inside the loop would resolve to *each chooser's* opponents — the opponent's iteration would then
 * draw from the Saga controller's own board, and there is no source-controller-anchored gather
 * predicate to pin it back. Instead we gather the shared pool once in the (unrebound) controller
 * context — `BattlefieldMatching(Permanent.opponentControls().manaValueAtLeast(3))`, whose controller
 * predicate resolves against the Saga controller — then run the APNAP "starting with you" picks as two
 * capped selects over that one collection: [Chooser.Controller] first, then [Chooser.Opponent] over the
 * *remainder* (`storeRemainder`), so a permanent an earlier chooser took can't be double-chosen. Both
 * picks are exiled after all choosing is done. (2-player scope — TLA is a 2-player set; `Chooser.Opponent`
 * routes to the single opponent.)
 *
 * **Chapter II** — a plain [MayEffect] over "target opponent draws 3, then you draw 3": one yes/no gates
 * the whole clause (the printed "If you do" — accept and both draw, decline and neither does).
 *
 * **Chapter III** — the standard transforming-Saga final chapter ([Effects.ExileAndReturnTransformed],
 * as on The Legend of Roku / Kuruk / The Rise of Sozin).
 *
 * The **back face** reuses the second-spell trigger ([Triggers.NthSpellCast]`(2, You)`, as on Breeches,
 * the Blastmaker) and the airbend primitive ([Effects.Airbend], the exile + fixed-{2}-recast-to-owner
 * tail) over an "up to one *other* target nonland permanent" requirement ([TargetOther] wrapping an
 * optional [TargetObject] — same shape as Aang, Swift Savior's ETB).
 */
private val AvatarYangchen = card("Avatar Yangchen") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Legendary Creature — Avatar"
    oracleText = "Flying\n" +
        "Whenever you cast your second spell each turn, airbend up to one other target nonland " +
        "permanent. (Exile it. While it's exiled, its owner may cast it for {2} rather than its " +
        "mana cost.)"
    power = 4
    toughness = 5

    keywords(Keyword.FLYING)

    // Whenever you cast your second spell each turn, airbend up to one other target nonland permanent.
    triggeredAbility {
        trigger = Triggers.NthSpellCast(2, Player.You)
        target(
            "up to one other target nonland permanent",
            TargetOther(
                baseRequirement = TargetObject(
                    count = 1,
                    optional = true,
                    filter = TargetFilter.NonlandPermanent
                )
            )
        )
        effect = Effects.Airbend()
        description = "Whenever you cast your second spell each turn, airbend up to one other " +
            "target nonland permanent."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "27"
        artist = "Kuno"
        flavorText = "\"Selfless duty calls you to sacrifice your own spiritual needs and do " +
            "whatever it takes to protect the world.\""
        imageUri = "https://cards.scryfall.io/normal/back/a/6/a60e8f23-90b2-4bc6-bd54-a95055556389.jpg?1764120067"
    }
}

private val TheLegendOfYangchenFront = card("The Legend of Yangchen") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter.)\n" +
        "I — Starting with you, each player chooses up to one permanent with mana value 3 or " +
        "greater from among permanents your opponents control. Exile those permanents.\n" +
        "II — You may have target opponent draw three cards. If you do, draw three cards.\n" +
        "III — Exile this Saga, then return it to the battlefield transformed under your control."

    // I — Starting with you, each player chooses up to one permanent with mana value 3 or greater
    // from among permanents your opponents control. Exile those permanents.
    sagaChapter(1) {
        effect = Effects.Composite(
            listOf(
                // The shared pool: permanents your opponents control with mana value 3 or greater.
                // Gathered once in the (unrebound) Saga-controller context, so opponentControls()
                // resolves against the Saga controller — the same pool for every chooser.
                GatherCardsEffect(
                    source = CardSource.BattlefieldMatching(
                        filter = GameObjectFilter.Permanent.opponentControls().manaValueAtLeast(3),
                        player = Player.Each
                    ),
                    storeAs = "yangchenPool"
                ),
                // Starting with you: you choose up to one of them.
                SelectFromCollectionEffect(
                    from = "yangchenPool",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    chooser = Chooser.Controller,
                    storeSelected = "yangchenControllerPick",
                    storeRemainder = "yangchenRemaining",
                    prompt = "Choose up to one permanent with mana value 3 or greater from among " +
                        "permanents your opponents control",
                    selectedLabel = "Exile",
                    useTargetingUI = true
                ),
                // Then the opponent chooses up to one of what's left (can't repeat your pick).
                SelectFromCollectionEffect(
                    from = "yangchenRemaining",
                    selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                    chooser = Chooser.Opponent,
                    storeSelected = "yangchenOpponentPick",
                    prompt = "Choose up to one permanent with mana value 3 or greater from among " +
                        "permanents your opponents control",
                    selectedLabel = "Exile",
                    useTargetingUI = true
                ),
                // Exile all the chosen permanents.
                MoveCollectionEffect(
                    from = "yangchenControllerPick",
                    destination = CardDestination.ToZone(Zone.EXILE)
                ),
                MoveCollectionEffect(
                    from = "yangchenOpponentPick",
                    destination = CardDestination.ToZone(Zone.EXILE)
                )
            )
        )
    }

    // II — You may have target opponent draw three cards. If you do, draw three cards.
    sagaChapter(2) {
        target("target opponent", TargetOpponent())
        effect = MayEffect(
            Effects.Composite(
                Effects.DrawCards(3, EffectTarget.ContextTarget(0)),
                Effects.DrawCards(3)
            )
        )
    }

    // III — Exile this Saga, then return it to the battlefield transformed under your control.
    sagaChapter(3) {
        effect = Effects.ExileAndReturnTransformed()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "27"
        artist = "Kuno"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a60e8f23-90b2-4bc6-bd54-a95055556389.jpg?1764120067"
    }
}

val TheLegendOfYangchen: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = TheLegendOfYangchenFront,
    backFace = AvatarYangchen,
)
