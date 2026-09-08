package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.CHAMPIONED_CARDS
import com.wingedsheep.sdk.scripting.effects.CHAMPION_CANDIDATES
import com.wingedsheep.sdk.scripting.effects.CHAMPION_CHOICE
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.EmitChampionedEventEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Printed reminder text for champion, parameterized by the quality — the Lorwyn wording, which is
 * what Scryfall carries on all nine champion cards.
 */
private fun championReminder(quality: String): String =
    "Champion $quality (When this enters, sacrifice it unless you exile another $quality you " +
        "control. When this leaves the battlefield, that card returns to the battlefield.)"

/**
 * The indefinite article for a quality noun. Champion's printed qualities are a creature type or
 * the word "creature"; only "Elemental" among the nine printed cards takes "an", but the rule is
 * general so a later Kindred quality gets it right too.
 */
private fun article(noun: String): String =
    if (noun.first().uppercaseChar() in "AEIOU") "an $noun" else "a $noun"

/**
 * Add Champion (CR 702.72, Lorwyn) — keyword + the pair of linked triggered abilities.
 *
 * > **CR 702.72a** — "Champion represents two triggered abilities. 'Champion an [object]' means
 * > 'When this permanent enters, sacrifice it unless you exile another [object] you control' and
 * > 'When this permanent leaves the battlefield, return the exiled card to the battlefield under
 * > its owner's control.'"
 * > **CR 702.72b / 607.2k** — the two abilities are linked; the second returns only what the first
 * > exiled.
 * > **CR 702.72c** — a permanent is "championed" by another permanent if the latter exiles the
 * > former as the direct result of a champion ability.
 *
 * The keyword is display-only; the behavior composes existing primitives, with no new executor:
 *
 *  - **Enters.** A Gather → Select → Move pipeline. `champion` says "exile another [object] you
 *    control" with no "target", so the permanent is **chosen, not targeted** — gathered with
 *    [CardSource.BattlefieldMatching] scoped to [Player.You] (projected control, so a permanent
 *    you stole this turn is eligible and one you lost is not) and to
 *    `GameObjectFilter.notSourceItself()` for "another". `notSourceItself` is *visit-aware*: it
 *    compares the resolving ability's originating battlefield visit, so a champion that blinked
 *    cannot exclude — or exile — the wrong instance of itself.
 *    [SelectionMode.ChooseUpTo]`(1)` is the "unless": picking nothing is always legal, and is the
 *    only option when you control no other matching permanent (the selection resolves without a
 *    prompt over an empty candidate set). The move carries `linkToSource`, which files the card in
 *    the *originating visit's* linked-exile pile.
 *  - **The "unless".** An [IfYouDoEffect] gate over that pipeline whose criterion is
 *    [SuccessCriterion.CollectionNonEmpty] on the move's `storeMovedAs` ([CHAMPIONED_CARDS]) — the
 *    cards that actually reached exile, not merely the ones picked. Its `otherwise` is
 *    [SacrificeSelfEffect]; its `then` is [EmitChampionedEventEffect], the CR 702.72c signal that
 *    drives Mistbind Clique's "when a Faerie is championed with this creature".
 *  - **Leaves.** An ordinary [Triggers.LeavesBattlefield] trigger running
 *    [Effects.ReturnLinkedExileUnderOwnersControl], which reads the same originating visit's pile.
 *
 * Modelling this as **two separate triggers** rather than an "exile until this leaves" replacement
 * is what CR 702.72a says and reproduces the printed interaction Fiend Hunter documents: if the
 * champion leaves the battlefield before its enters trigger resolves, the leaves trigger resolves
 * first against an empty pile and does nothing, and the enters trigger then exiles a permanent that
 * never comes back. It also means the sacrifice is a genuine no-op when the champion is already
 * gone, exactly as the printed instruction behaves.
 *
 * Multiple instances would install two independent pairs; no printed card has two, and because both
 * halves would share one linked-exile pile per battlefield visit, a card that needs two would have
 * to distinguish the pairs first (CR 607.2k links each pair separately).
 *
 * @param quality The [object] to champion, as a filter over **permanents** — champion's printed
 *   quality is a bare tribal noun ("a Goblin"), which per CR 109.2 reads as a Goblin *permanent*,
 *   so a Kindred noncreature Goblin is a legal choice. Use the [Subtype] overload for those; pass
 *   `GameObjectFilter.Creature` for "champion a creature".
 * @param qualityDescription The quality with its article, as printed ("a Goblin", "a creature").
 */
fun CardBuilder.champion(quality: GameObjectFilter, qualityDescription: String) {
    keywordSet.add(Keyword.CHAMPION)

    triggeredAbilities.add(
        TriggeredAbility.create(
            trigger = Triggers.EntersBattlefield.event,
            binding = Triggers.EntersBattlefield.binding,
            effect = IfYouDoEffect(
                action = CompositeEffect(
                    listOf(
                        GatherCardsEffect(
                            source = CardSource.BattlefieldMatching(
                                filter = quality.notSourceItself(),
                                player = Player.You
                            ),
                            storeAs = CHAMPION_CANDIDATES
                        ),
                        SelectFromCollectionEffect(
                            from = CHAMPION_CANDIDATES,
                            selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                            storeSelected = CHAMPION_CHOICE,
                            // Choosing among permanents in play: keep it on the battlefield rather
                            // than behind a card-list overlay, so counters, auras and board context
                            // stay visible while deciding what to give up.
                            useTargetingUI = true,
                            prompt = "Exile another $qualityDescription you control, " +
                                "or choose nothing to sacrifice this permanent",
                            selectedLabel = "Exile"
                        ),
                        MoveCollectionEffect(
                            from = CHAMPION_CHOICE,
                            destination = CardDestination.ToZone(Zone.EXILE),
                            linkToSource = true,
                            storeMovedAs = CHAMPIONED_CARDS
                        )
                    )
                ),
                ifYouDo = EmitChampionedEventEffect(),
                ifYouDont = SacrificeSelfEffect,
                successCriterion = SuccessCriterion.CollectionNonEmpty(CHAMPIONED_CARDS)
            ),
            descriptionOverride = championReminder(qualityDescription)
        )
    )

    triggeredAbilities.add(
        TriggeredAbility.create(
            trigger = Triggers.LeavesBattlefield.event,
            binding = Triggers.LeavesBattlefield.binding,
            effect = Effects.ReturnLinkedExileUnderOwnersControl(),
            descriptionOverride = "When this permanent leaves the battlefield, return the exiled " +
                "card to the battlefield under its owner's control."
        )
    )
}

/**
 * "Champion a [subtype]" — the tribal form, which is eight of the nine printed champion cards.
 *
 * The quality is a **permanent** filter, not a creature filter: CR 109.2 reads a bare tribal noun
 * as any permanent with that type, so a Kindred noncreature Faerie is a legal thing to champion
 * with Mistbind Clique. Filtering to creatures here would silently narrow the choice.
 */
fun CardBuilder.champion(subtype: Subtype) =
    champion(
        quality = GameObjectFilter.Permanent.withSubtype(subtype),
        qualityDescription = article(subtype.value)
    )

/** "Champion a creature" — Changeling Berserker / Hero / Titan. */
fun CardBuilder.championCreature() =
    champion(quality = GameObjectFilter.Creature, qualityDescription = "a creature")
