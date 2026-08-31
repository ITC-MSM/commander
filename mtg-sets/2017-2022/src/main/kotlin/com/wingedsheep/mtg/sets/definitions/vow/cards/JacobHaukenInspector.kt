package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantMayCastFromLinkedExile
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Jacob Hauken, Inspector // Hauken's Insight — Innistrad: Crimson Vow #65
 * {1}{U} · Legendary Creature — Human Advisor 0/2 // Legendary Enchantment
 *
 * Front — Jacob Hauken, Inspector
 *   {T}: Draw a card, then exile a card from your hand face down. You may look at that card for
 *   as long as it remains exiled. You may pay {4}{U}{U}. If you do, transform Jacob Hauken.
 *
 * Back — Hauken's Insight
 *   At the beginning of your upkeep, exile the top card of your library face down. You may look
 *   at that card for as long as it remains exiled.
 *   Once during each of your turns, you may play a land or cast a spell from among the cards
 *   exiled with this permanent without paying its mana cost.
 *
 * Modeling notes:
 *
 *  - **One exile pile, two faces.** Both faces exile with `linkToSource = true`, so the cards the
 *    *creature* squirreled away are the cards the *enchantment* can later play — which is the
 *    whole card. Transforming does not create a new object (CR 712.9), so the
 *    `LinkedExileComponent` written by the front face is still there for the back face's grant to
 *    read.
 *  - **"You may look at that card" is a look grant, not a play grant.** Exile has no
 *    controller baseline for face-down cards (CR 708.5 — "You can't look at face-down cards in any
 *    other zone"), so without an explicit grant the front face would hide your own exiled hand
 *    cards from you until the flip. `MoveCollectionEffect.lookableInExile` is that grant, and it
 *    is deliberately separate from any permission to play: on the front face you may look and
 *    nothing more.
 *  - **The back face's ability is [GrantMayCastFromLinkedExile], not a fresh may-play grant per
 *    exiled card.** "From among the cards exiled with this permanent" *is* the linked-exile pile,
 *    and the static recomputes on every read, so cards keep arriving each upkeep without any
 *    per-card bookkeeping. `filter = Any` is what lets a land be played — a land is played, never
 *    cast (CR 305.1), and it is the same one-per-turn allowance either way, which is what the
 *    printed "**or**" means.
 *  - **The transform clause is a plain `MayPayMana`.** It is part of the activated ability's
 *    resolution, not a second ability, so declining costs nothing and the draw + exile still
 *    happened. `{4}{U}{U}` is paid at resolution.
 *  - **The exile is mandatory and the hand is never empty when it happens**: the ability draws
 *    first, so `ChooseExactly(1)` always has a card to offer.
 */
private val JacobHaukenInspectorFront = card("Jacob Hauken, Inspector") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Advisor"
    power = 0
    toughness = 2
    oracleText = "{T}: Draw a card, then exile a card from your hand face down. You may look at " +
        "that card for as long as it remains exiled. You may pay {4}{U}{U}. If you do, transform " +
        "Jacob Hauken."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.Composite(
            Effects.DrawCards(1),
            GatherCardsEffect(
                source = CardSource.FromZone(Zone.HAND, Player.You),
                storeAs = "haukenHand",
            ),
            SelectFromCollectionEffect(
                from = "haukenHand",
                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                storeSelected = "haukenExiled",
                prompt = "Choose a card to exile face down",
            ),
            MoveCollectionEffect(
                from = "haukenExiled",
                destination = CardDestination.ToZone(Zone.EXILE),
                faceDown = FaceDownMode.HIDDEN,
                linkToSource = true,
                lookableInExile = true,
            ),
            MayPayManaEffect(
                cost = ManaCost.parse("{4}{U}{U}"),
                effect = TransformEffect(EffectTarget.Self),
            ),
        )
        description = "{T}: Draw a card, then exile a card from your hand face down. You may look " +
            "at that card for as long as it remains exiled. You may pay {4}{U}{U}. If you do, " +
            "transform Jacob Hauken."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "65"
        artist = "Aurore Folny"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b4529c3-8edb-4909-b910-806450a39d2e.jpg?1783924901"
    }
}

private val HaukensInsight = card("Hauken's Insight") {
    manaCost = ""
    colorIdentity = "U"
    colorIndicator = "U" // Transformed back face, no mana cost (CR 204).
    typeLine = "Legendary Enchantment"
    oracleText = "At the beginning of your upkeep, exile the top card of your library face down. " +
        "You may look at that card for as long as it remains exiled.\n" +
        "Once during each of your turns, you may play a land or cast a spell from among the cards " +
        "exiled with this permanent without paying its mana cost."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1)),
                storeAs = "haukensInsightExiled",
            ),
            MoveCollectionEffect(
                from = "haukensInsightExiled",
                destination = CardDestination.ToZone(Zone.EXILE),
                faceDown = FaceDownMode.HIDDEN,
                linkToSource = true,
                lookableInExile = true,
            ),
        )
        description = "At the beginning of your upkeep, exile the top card of your library face " +
            "down. You may look at that card for as long as it remains exiled."
    }

    staticAbility {
        ability = GrantMayCastFromLinkedExile(
            filter = GameObjectFilter.Any,
            duringYourTurnOnly = true,
            withoutPayingManaCost = true,
            oncePerTurn = true,
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "65"
        artist = "Aurore Folny"
        imageUri = "https://cards.scryfall.io/normal/back/6/b/6b4529c3-8edb-4909-b910-806450a39d2e.jpg?1783924901"

        ruling(
            "2021-11-19",
            "Paying {4}{U}{U} and transforming Jacob Hauken, Inspector is part of the resolution " +
                "of the activated ability. You draw a card and exile a card before choosing " +
                "whether to pay."
        )
        ruling(
            "2021-11-19",
            "Playing a card using the last ability of Hauken's Insight follows all the normal " +
                "timing rules for that card. For example, if you play a land this way, you may do " +
                "so only during your main phase while the stack is empty and only if you haven't " +
                "yet played a land (unless another effect allows you to play additional lands)."
        )
        ruling(
            "2021-11-19",
            "You may play cards this way that were exiled with Jacob Hauken, Inspector before it " +
                "transformed into Hauken's Insight."
        )
        ruling(
            "2021-11-19",
            "Once Hauken's Insight leaves the battlefield, the player that controlled it may no " +
                "longer play the exiled cards. If it's destroyed and somehow returns to the " +
                "battlefield, it's a new object with no memory of the previously exiled cards."
        )
    }
}

val JacobHaukenInspector: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = JacobHaukenInspectorFront,
    backFace = HaukensInsight,
)
