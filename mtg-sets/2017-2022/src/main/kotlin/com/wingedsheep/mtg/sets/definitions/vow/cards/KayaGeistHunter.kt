package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MultiplyTokenCreation
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.events.ControllerFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Kaya, Geist Hunter — Innistrad: Crimson Vow #240
 * {1}{W}{B} · Legendary Planeswalker — Kaya · Mythic · Starting loyalty 3
 *
 * +1: Creatures you control gain deathtouch until end of turn. Put a +1/+1 counter on up to one
 *     target creature token you control.
 * −2: Until end of turn, if one or more tokens would be created under your control, twice that
 *     many of those tokens are created instead.
 * −6: Exile all cards from all graveyards, then create a 1/1 white Spirit creature token with
 *     flying for each card exiled this way.
 *
 * Modeling notes:
 *
 *  - **The +1 is two independent sentences.** The keyword grant is [Effects.ForEachInGroup] over
 *    `Creature.youControl()` with [EffectTarget.Self] naming the iteration entity — the group is
 *    snapshotted as the ability resolves, so a creature that arrives afterwards does not gain
 *    deathtouch. The counter rides an **optional** ("up to one") target restricted to a creature
 *    *token* you control, so the ability resolves fine with nothing chosen.
 *  - **The −2 is a durational replacement effect, not a static ability.** It is the same
 *    [MultiplyTokenCreation] that Doubling Season and Ojer Taq print, handed to
 *    [Effects.GrantReplacementEffect] with `Duration.EndOfTurn`; the grant lands in
 *    `GameState.grantedReplacementEffects` and the token-count read path enumerates grants
 *    alongside permanents' printed replacements. Two consequences fall straight out of that
 *    channel and are both correct: the doubler **stacks multiplicatively** with a printed one
 *    (Kaya + Doubling Season turns two Soldiers into eight), and it **survives Kaya leaving the
 *    battlefield** — the loyalty ability is a one-shot that has finished resolving, and the
 *    continuous effect it created lasts until the cleanup step regardless (CR 611.2b). The
 *    printed ruling that the extra tokens are "exact copies of the tokens that were being created
 *    in the first place" is what a count multiplication means here: one creation event, twice the
 *    count, identical characteristics.
 *  - **The −6 is the Gather → Move → count pipeline**, the same shape Quintorius Kand uses:
 *    `CardSource.FromZone(GRAVEYARD, Player.Each)` gathers *every* card in *every* graveyard
 *    (not just creature cards — this Kaya exiles all of them), `MoveCollectionEffect` exiles them
 *    and stores what actually moved, and the Spirit count reads that moved collection through
 *    [DynamicAmount.DistinctEntitiesInCollections]. Reading the *moved* collection rather than
 *    the gathered one is what makes the count honest when a card is replaced on its way out.
 */
val KayaGeistHunter = card("Kaya, Geist Hunter") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Planeswalker — Kaya"
    startingLoyalty = 3
    oracleText = "+1: Creatures you control gain deathtouch until end of turn. Put a +1/+1 " +
        "counter on up to one target creature token you control.\n" +
        "−2: Until end of turn, if one or more tokens would be created under your control, " +
        "twice that many of those tokens are created instead.\n" +
        "−6: Exile all cards from all graveyards, then create a 1/1 white Spirit creature token " +
        "with flying for each card exiled this way."

    // +1: Creatures you control gain deathtouch until end of turn. Put a +1/+1 counter on up to
    //     one target creature token you control.
    loyaltyAbility(+1) {
        val tokenCreature = target(
            "up to one target creature token you control",
            TargetCreature(
                optional = true,
                filter = TargetFilter(GameObjectFilter.Creature.token().youControl())
            )
        )
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.youControl()),
                Effects.GrantKeyword(Keyword.DEATHTOUCH, EffectTarget.Self)
            ),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, tokenCreature)
        )
        description = "Creatures you control gain deathtouch until end of turn. Put a +1/+1 " +
            "counter on up to one target creature token you control."
    }

    // −2: Until end of turn, if one or more tokens would be created under your control, twice
    //     that many of those tokens are created instead.
    loyaltyAbility(-2) {
        effect = Effects.GrantReplacementEffect(
            MultiplyTokenCreation(
                factor = 2,
                appliesTo = EventPattern.TokenCreationEvent(controller = ControllerFilter.You)
            )
        )
        description = "Until end of turn, if one or more tokens would be created under your " +
            "control, twice that many of those tokens are created instead."
    }

    // −6: Exile all cards from all graveyards, then create a 1/1 white Spirit creature token with
    //     flying for each card exiled this way.
    loyaltyAbility(-6) {
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.Each,
                        filter = GameObjectFilter.Any
                    ),
                    storeAs = "kayaGraveyards"
                ),
                MoveCollectionEffect(
                    from = "kayaGraveyards",
                    destination = CardDestination.ToZone(Zone.EXILE),
                    storeMovedAs = "kayaExiled"
                ),
                Effects.CreateToken(
                    count = DynamicAmount.DistinctEntitiesInCollections(listOf("kayaExiled")),
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.WHITE),
                    creatureTypes = setOf("Spirit"),
                    keywords = setOf(Keyword.FLYING)
                )
            )
        )
        description = "Exile all cards from all graveyards, then create a 1/1 white Spirit " +
            "creature token with flying for each card exiled this way."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "240"
        artist = "Ryan Pancoast"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a98a208c-ee2b-4672-a43b-f4a708585b1a.jpg?1783924793"

        ruling("2021-11-19", "The tokens that Kaya's second ability creates are exact copies of the tokens that were being created in the first place.")
    }
}
