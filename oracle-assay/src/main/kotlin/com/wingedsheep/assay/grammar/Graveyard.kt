package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.Phrase
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ChooseCreatureTypeEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.assay.syntax.bind
import com.wingedsheep.assay.syntax.phrase
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetRequirement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Clauses that reach into a graveyard — "Return target creature card from your graveyard to your
 * hand."
 *
 * The first rules whose target is **not on the battlefield**, which is the whole reason they are a
 * file: [Targets.permanent] mints a battlefield `TargetObject` and its inverse refuses anything
 * else, deliberately, so that "destroy target creature" cannot print a script pointing at a
 * graveyard. A graveyard target is the same `TargetObject` with a `zone` and an owner predicate, and
 * both are printed by the noun phrase — "**your** graveyard" is the owner and "from your graveyard"
 * is the zone.
 *
 * The noun ends in "card" rather than naming a permanent, which is Oracle's own distinction: an
 * object in a graveyard is a *card*, not a permanent, and the bare type nouns [Filters] spells are
 * the modifier in front of it. That noun is [Filters.cardNoun] — a whole noun phrase, head word
 * included — and not a `{filter}` slot with the word "card" after it in each template. The
 * difference is what lets a suffix clause exist here at all: "return target creature card **with
 * mana value 3 or less** from your graveyard" attaches behind the head noun, which a sentence-owned
 * "card" left no room for.
 *
 * Its "target card" row is therefore the `Any` row of that vocabulary rather than a rule of its own.
 * It was one until this band; a second rule for the value the general one now prints is the
 * redundant-readings configuration this module gates on.
 */
object Graveyard {

    /** "target creature card from your graveyard" — the requirement half. */
    private fun inYourGraveyard(filter: GameObjectFilter): TargetRequirement =
        TargetObject(filter = TargetFilter(filter.ownedByYou(), zone = Zone.GRAVEYARD), id = Targets.SLOT)

    /** The inverse: the filter a your-graveyard requirement restricts to, or null for anything else. */
    private fun graveyardFilter(requirement: TargetRequirement): GameObjectFilter? {
        val base = (requirement as? TargetObject)?.filter?.baseFilter ?: return null
        val unowned = base.copy(controllerPredicate = null)
        return unowned.takeIf { requirement == inYourGraveyard(it) }
    }

    /**
     * The shape: a verb, one card targeted in your graveyard, and nothing else.
     *
     * Two members — to your hand and onto the battlefield — differing only in the destination, which
     * is a different English sentence rather than a different word, so each spells its own template.
     */
    private fun graveyardStep(
        template: String,
        name: String,
        effect: (com.wingedsheep.sdk.scripting.targets.EffectTarget) -> com.wingedsheep.sdk.scripting.effects.Effect,
    ): Phrase<CardScript> {
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = effect(Targets.bound()),
            targetRequirements = listOf(inYourGraveyard(filter)),
        )
        return phrase(template, name = name) {
            slot("filter", Filters.cardNoun)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() ?: return@match null
                val filter = graveyardFilter(requirement) ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * "Exile target card from a graveyard." — Withered Wretch.
     *
     * The one graveyard target that is **not** yours: "a graveyard" is any player's, which the model
     * says by carrying no owner predicate at all. That is why it is its own rule rather than a row
     * of [graveyardStep] — the shape there scopes every filter with `ownedByYou()`, deliberately, so
     * that "from your graveyard" cannot print a requirement that reaches an opponent's.
     */
    private val exileAnyCardFromAGraveyard: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = Effects.Move(Targets.bound(), Zone.EXILE),
            targetRequirements = listOf(TargetObject(filter = TargetFilter.CardInGraveyard, id = Targets.SLOT)),
        )
        phrase("exile target card from a graveyard", name = "exile a card from any graveyard") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Return up to two target Bird and/or Cleric permanent cards from your graveyard to the
     * battlefield." — Celestial Gatekeeper's death trigger, second clause.
     *
     * The counted sibling of [graveyardStep], and positional rather than named for
     * [Combat.returnOneOrTwoTargets]' reason: the iteration rebinds slot 0 per target, so a named
     * reference would name the whole declaration. "Up to" is the requirement's `optional` flag.
     */
    private val returnUpToSeveralFromGraveyard: Phrase<CardScript> = run {
        fun scriptFor(count: Int, filter: GameObjectFilter) = CardScript(
            spellEffect = ForEachTargetEffect(
                listOf(Effects.PutOntoBattlefieldFromGraveyard(EffectTarget.ContextTarget(0)))
            ),
            targetRequirements = listOf(
                TargetObject(
                    count = count,
                    optional = true,
                    filter = TargetFilter(filter.ownedByYou(), zone = Zone.GRAVEYARD),
                )
            ),
        )
        phrase(
            "return up to {n} target {filter} from your graveyard to the battlefield",
            name = "return several cards from your graveyard to the battlefield",
        ) {
            slot("n", Cardinals.word)
            // The plural card noun, which inflects only its head: "Bird and/or Cleric permanent
            // **cards**". That split used to be made here by spelling the noun in the template; it
            // belongs to [Filters.cardNoun], which is what lets the phrase carry a suffix clause.
            slot("filter", Filters.pluralCards)
            build { scriptFor(it.int("n"), it.value("filter")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() as? TargetObject ?: return@match null
                val filter = requirement.filter.baseFilter.copy(controllerPredicate = null)
                if (!Cardinals.spellable(requirement.count)) return@match null
                if (script != scriptFor(requirement.count, filter)) return@match null
                bind("n" to requirement.count, "filter" to filter)
            }
        }
    }

    /**
     * "You may put target creature card from that player's graveyard onto the battlefield under your
     * control." — Scion of Darkness.
     *
     * "That player" is the one the combat-damage trigger named, and the model says so with an
     * *owner* predicate rather than a player reference: a card in a graveyard is owned by the player
     * whose graveyard it is. So the requirement is the opponent-owned graveyard filter and the
     * phrase's "that player's" is a literal — a rule that slotted it would need a player vocabulary
     * for a phrase with one reading.
     */
    private val putTargetFromThatPlayersGraveyard: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = MayEffect(Effects.PutOntoBattlefieldUnderYourControl(Targets.bound())),
            targetRequirements = listOf(
                TargetObject(filter = TargetFilter.CreatureInGraveyard.ownedByOpponent(), id = Targets.SLOT)
            ),
        )
        phrase(
            "you may put target creature card from that player's graveyard onto the battlefield " +
                "under your control",
            name = "put a card from the triggering player's graveyard onto the battlefield",
        ) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Return all Zombie cards from all graveyards to their owners' hands." — Infernal Caretaker.
     *
     * "All graveyards" is a per-player iteration in the model, and "their owners'" is what makes the
     * inner gather and move both say `Player.You` — inside a `ForEachPlayer` the pronoun rebinds to
     * the player being processed. That rebinding is the whole reason this is one rule rather than a
     * group sweep: nothing in [Steps]' mass vocabulary reaches a non-battlefield zone.
     */
    private val returnAllOfSubtypeFromAllGraveyards: Phrase<CardScript> = run {
        fun scriptFor(subtype: Subtype) = CardScript(
            spellEffect = ForEachPlayerEffect(
                players = Player.Each,
                effects = listOf(
                    GatherCardsEffect(
                        source = CardSource.FromZone(
                            Zone.GRAVEYARD,
                            Player.You,
                            GameObjectFilter.Any.withSubtype(subtype),
                        ),
                        storeAs = "zombies",
                    ),
                    MoveCollectionEffect(
                        from = "zombies",
                        destination = CardDestination.ToZone(Zone.HAND, Player.You),
                    ),
                ),
            )
        )
        phrase(
            "return all {subtype} cards from all graveyards to their owners' hands",
            name = "return every card of a subtype from all graveyards",
        ) {
            slot("subtype", Primitives.subtype)
            build { scriptFor(it.value("subtype")) }
            match { script ->
                val body = (script.spellEffect as? ForEachEffect)?.body as? CompositeEffect ?: return@match null
                val gather = body.effects.firstOrNull() as? GatherCardsEffect ?: return@match null
                val filter = (gather.source as? CardSource.FromZone)?.filter ?: return@match null
                val subtype = filter.cardPredicates.filterIsInstance<CardPredicate.HasSubtype>()
                    .singleOrNull()?.subtype ?: return@match null
                if (script != scriptFor(subtype)) return@match null
                bind("subtype" to subtype)
            }
        }
    }

    /**
     * "Choose a creature type. Shuffle all creature cards of that type from your graveyard into your
     * library." — Elvish Soultiller.
     *
     * Two printed sentences and one recipe, and the link between them is a *pipeline* rather than a
     * word: the selection reads the type the first sentence chose (`matchChosenCreatureType`), so
     * neither sentence denotes anything alone. The same shape [CreatureTypes] uses for the
     * battlefield-side choose-a-type effects, one zone over.
     */
    private val shuffleChosenTypeFromGraveyard: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = Effects.Composite(
                listOf(
                    ChooseCreatureTypeEffect,
                    GatherCardsEffect(
                        source = CardSource.FromZone(Zone.GRAVEYARD, Player.You, GameObjectFilter.Creature),
                        storeAs = "graveyardCreatures",
                    ),
                    SelectFromCollectionEffect(
                        from = "graveyardCreatures",
                        selection = SelectionMode.All,
                        matchChosenCreatureType = true,
                        storeSelected = "chosen",
                    ),
                    MoveCollectionEffect(
                        from = "chosen",
                        destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Shuffled),
                    ),
                )
            )
        )
        phrase(
            "choose a creature type. shuffle all creature cards of that type from your graveyard " +
                "into your library",
            name = "shuffle a chosen type out of your graveyard",
        ) {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /**
     * "Return all land cards from your graveyard to the battlefield tapped." — Splendid Reclamation,
     * Resurgent Belief, Wake the Past, Triumphant Reckoning, and the resolution half of Aftermath
     * Analyst.
     *
     * The **mass** counterpart of this file's targeted returns, and it is a different model rather
     * than a plural spelling of one: nothing is targeted, so there is no `TargetRequirement` at all
     * and the cards are collected on resolution by a gather → move pipeline. That is also why the
     * noun is [Filters.pluralCards] and not the targeted rows' [Filters.cardNoun] — "all land cards"
     * names a set, not a chosen one.
     *
     * `tapped` is the row axis, exactly as it is on [Tokens]' create clause: Oracle prints the
     * sentence with and without the word (Splendid Reclamation against Resurgent Belief),
     * `ZonePlacement` carries a distinct value for each, and freezing either into the template would
     * make the other a rule nobody had written. So it is two instantiations of one shape.
     *
     * ### Finding: `storeAs` is a presentation-class field the differential still compares
     *
     * A pipeline's collection name is wiring — it links the gather to the move and no printed word
     * determines it — but it is not in `Folds.dropPresentation`, so the grammar has to guess the
     * *same string* a hand-written card happened to pick. The corpus spells this sentence's
     * collection `graveyard_lands` on both cards that write it (Aftermath Analyst, Summon Titan) and
     * nothing else, so that is the canonical here, filter-general rule and land-specific name
     * notwithstanding. Canonicalizing collection names positionally in the differential would retire
     * the guess for every pipeline family at once; that is its own change, and this rule is one more
     * argument for it.
     */
    private fun returnAllFromYourGraveyard(tapped: Boolean): Phrase<CardScript> {
        val suffix = if (tapped) " tapped" else ""
        val placement = if (tapped) ZonePlacement.Tapped else ZonePlacement.Default
        fun scriptFor(filter: GameObjectFilter) = CardScript(
            spellEffect = Effects.Composite(
                GatherCardsEffect(
                    source = CardSource.FromZone(Zone.GRAVEYARD, Player.You, filter),
                    storeAs = COLLECTED,
                ),
                MoveCollectionEffect(
                    from = COLLECTED,
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = placement),
                ),
            )
        )
        return phrase(
            "return all {filter} from your graveyard to the battlefield$suffix",
            name = "return every matching card from your graveyard to the battlefield$suffix",
        ) {
            slot("filter", Filters.pluralCards)
            build { scriptFor(it.value("filter")) }
            match { script ->
                val steps = (script.spellEffect as? CompositeEffect)?.effects ?: return@match null
                val gather = steps.firstOrNull() as? GatherCardsEffect ?: return@match null
                val filter = (gather.source as? CardSource.FromZone)?.filter ?: return@match null
                if (script != scriptFor(filter)) return@match null
                bind("filter" to filter)
            }
        }
    }

    /**
     * "Target player shuffles up to four target cards from their graveyard into their library." —
     * Witness the Future, Gaea's Blessing, Rite of Renewal.
     *
     * The counted sibling of [returnUpToSeveralFromGraveyard] with the other destination, and the
     * one place in this file whose requirement carries **no owner predicate**: the printed noun is
     * a bare "target cards", the graveyard is named by the *player* the sentence targets rather
     * than by "your", and a card in any graveyard is therefore legal. That is
     * [TargetFilter.CardInGraveyard] — the same `Any`-in-a-graveyard requirement
     * [exileAnyCardFromAGraveyard] mints, counted and optional.
     *
     * **The shuffle rides the move.** `Effects.ShuffleIntoLibrary` is `ZonePlacement.Shuffled`, one
     * effect per card, and the library it shuffles is the card's *owner's* — which is what the
     * sentence says, since "their library" is the same player as "their graveyard". A separate
     * `ShuffleLibraryEffect()` step would shuffle the spell's controller's library instead, and
     * that is only right when the targets happen to be your own cards.
     *
     * **"Target player" is not modelled.** The printed sentence targets a player as well as the
     * cards, and the SDK has no way to scope an object target to another target's graveyard — a
     * `TargetPlayer` requirement beside these would let a card announce player A and cards from
     * B's graveyard. So the player is unread rather than half-read: it costs the "the player was
     * made an illegal target" ruling and nothing else, and the alternative is a model the engine
     * would enforce wrongly. Naming it properly is `mtg-sdk` work — a cross-target scope on
     * `TargetFilter` — and is reported here rather than approximated.
     */
    private val shuffleUpToSeveralIntoLibrary: Phrase<CardScript> = run {
        fun scriptFor(count: Int) = CardScript(
            spellEffect = ForEachTargetEffect(
                listOf(Effects.ShuffleIntoLibrary(EffectTarget.ContextTarget(0)))
            ),
            targetRequirements = listOf(
                TargetObject(count = count, optional = true, filter = TargetFilter.CardInGraveyard),
            ),
        )
        phrase(
            "target player shuffles up to {n} target cards from their graveyard into their library",
            name = "shuffle several cards from a graveyard into their owner's library",
        ) {
            slot("n", Cardinals.word)
            build { scriptFor(it.int("n")) }
            match { script ->
                val requirement = script.targetRequirements.singleOrNull() as? TargetObject
                    ?: return@match null
                if (!Cardinals.spellable(requirement.count)) return@match null
                if (script != scriptFor(requirement.count)) return@match null
                bind("n" to requirement.count)
            }
        }
    }

    /**
     * "Exile a card from a graveyard." — the Crimson Vow cemetery cycle (Gatekeeper, Illuminator,
     * Protector, Prowler, Desecrator), Lazav, Familiar Stranger, Kaya, Spirits' Justice.
     *
     * The **untargeted** sibling of [exileAnyCardFromAGraveyard], and the article is the whole
     * difference: "exile **target** card from a graveyard" is a cast-time choice the opponent can
     * respond to and this one is a choice made on resolution, so it declares no
     * `TargetRequirement` at all and collects the candidates itself — gather every graveyard,
     * choose one, move it. Reading the two into one model would be reading a targeting rule off a
     * word Oracle uses to mean the opposite.
     *
     * "A graveyard" is every player's, which the gather says with `Player.Each`; the chooser is the
     * ability's controller, which is `SelectFromCollectionEffect`'s default and what the sentence
     * means with no other player named.
     *
     * The clause is deliberately **not** counted. Oracle prints "a card" here and nothing else —
     * "exile **another** card from a graveyard" (Cemetery Desecrator) is a different sentence with
     * an exclusion this rule has no field for, and it declines rather than losing the word.
     *
     * ### `linkToSource` is not on this line, and is derived by the fold
     *
     * CR 607 makes this ability *linked* to a later one that says "the exiled card", and the SDK
     * carries that fact twice — on the move (`MoveCollectionEffect.linkToSource`) and on the read
     * (`EntityReference.LinkedExiledCard`). Only the read is printed. So this rule builds the plain
     * move and [CardFragment.deriveExileLinkage] sets the flag when some other line on the same
     * card turns out to read the pile, which is this module's "a value the SDK carries twice is
     * derived, not spelled" applied one scope out: the deriving evidence is on a different line, so
     * the derivation is in the fold rather than in the rule.
     */
    private val exileACardFromAGraveyard: Phrase<CardScript> = run {
        val script = CardScript(
            spellEffect = CompositeEffect(
                listOf(
                    GatherCardsEffect(
                        source = CardSource.FromZone(Zone.GRAVEYARD, Player.Each, GameObjectFilter.Any),
                        storeAs = GRAVEYARDS,
                    ),
                    SelectFromCollectionEffect(
                        from = GRAVEYARDS,
                        selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                        storeSelected = EXILED,
                        prompt = "Exile a card from a graveyard",
                        selectedLabel = "Exile",
                        useTargetingUI = true,
                    ),
                    MoveCollectionEffect(
                        from = EXILED,
                        destination = CardDestination.ToZone(Zone.EXILE),
                    ),
                )
            )
        )
        phrase("exile a card from a graveyard", name = "exile a chosen card from any graveyard") {
            build { script }
            match { if (it == script) bind() else null }
        }
    }

    /** The collection name [returnAllFromYourGraveyard] shares with the corpus; see its KDoc. */
    private const val COLLECTED = "graveyard_lands"

    /**
     * [exileACardFromAGraveyard]'s two collection names.
     *
     * Wiring, in the sense [returnAllFromYourGraveyard]'s KDoc records: no printed word determines
     * them and the differential compares them anyway, so the grammar picks a pair and the
     * hand-written cards spell the same one.
     */
    private const val GRAVEYARDS = "graveyards"
    private const val EXILED = "exiled"

    val clauses: List<Phrase<CardScript>> = listOf(
        exileAnyCardFromAGraveyard,
        exileACardFromAGraveyard,
        shuffleUpToSeveralIntoLibrary,
        returnAllFromYourGraveyard(tapped = true),
        returnAllFromYourGraveyard(tapped = false),
        shuffleChosenTypeFromGraveyard,
        returnUpToSeveralFromGraveyard,
        putTargetFromThatPlayersGraveyard,
        returnAllOfSubtypeFromAllGraveyards,
        graveyardStep(
            "return target {filter} from your graveyard to your hand",
            "return a card from your graveyard to your hand",
            // No `fromZone` guard here, unlike the battlefield row below — deliberate, not an
            // oversight in the asymmetry. `Effects.Move(_, HAND)` is what the corpus writes: of the
            // 198 hand-written cards whose oracle text says "from your graveyard to your hand",
            // exactly two set `fromZone`, and both are self-returns (Redtooth Vanguard, Squee,
            // Goblin Nabob) that this rule never generates — `Recursion.kt` owns those. The 113
            // targeted returns this row does produce write no guard at all.
        ) { Effects.Move(it, Zone.HAND) },
        graveyardStep(
            "return target {filter} from your graveyard to the battlefield",
            "return a card from your graveyard to the battlefield",
            // The guarded return: `fromZone = GRAVEYARD` skips the move if the card has left the
            // graveyard by resolution. Dropping it was tried, on the reading that the target
            // requirement's own `zone = GRAVEYARD` already decides legality — the differential
            // answered immediately, fixing three cards and breaking six. The corpus keeps the guard.
        ) { Effects.PutOntoBattlefieldFromGraveyard(it) },
    )
}
