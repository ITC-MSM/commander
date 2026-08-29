package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.dsl.Conditions as SdkConditions
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The spell-cost band — `ModifySpellCost` read as three vocabularies rather than one rule per
 * printed sentence.
 *
 * The cases below are chosen so that each names one *axis* of [SpellCosts] against a card that
 * prints it: the subject (Glowrider, Edgewalker, Undead Warchief), the direction and the
 * generic/coloured split (Glowrider against Edgewalker), and each of the five clauses (Bolt Bend,
 * Grounded for Life, Vanquish the Horde, Fungal Colossus, Geyser Drake).
 *
 * The refusal cases are the ones that matter most, as everywhere else in this module: each is a
 * value that would round-trip byte-exactly under a rule that inspected less than the whole model.
 */
class SpellCostsTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun declines(line: String) {
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    fun statics(line: String) = fragment(line).script.staticAbilities

    // Glowrider — the subject that is everyone's spells, and the increase direction.
    "a fixed increase is read on the anyone-casts subject" {
        statics("Noncreature spells cost {1} more to cast.") shouldBe listOf(
            ModifySpellCost(
                target = SpellCostTarget.AnyCaster(GameObjectFilter.Noncreature),
                modification = CostModification.IncreaseGeneric(1),
            )
        )
        roundTrips("Noncreature spells cost {1} more to cast.")
        roundTrips("Creature spells cost {2} more to cast.")
    }

    // The generic/coloured split is decided by the printed cost and nothing else, so `{1}` and
    // `{W}{B}` in the same sentence reach two different SDK families. Edgewalker is the coloured one.
    "a coloured reduction is a different modification from a generic one" {
        statics("Cleric spells you cast cost {W}{B} less to cast.") shouldBe listOf(
            ModifySpellCost(
                target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype(Subtype("Cleric"))),
                modification = CostModification.ReduceColored("{W}{B}"),
            )
        )
        roundTrips("Cleric spells you cast cost {W}{B} less to cast.")
        roundTrips("Creature spells you cast cost {1} less to cast.")
        roundTrips("Spells you cast cost {1} less to cast.")
    }

    // Undead Warchief. A spell's bare subtype is `Any.withSubtype`, not the battlefield noun
    // phrase's `Permanent.withSubtype` — a spell on the stack is not a permanent, which is the
    // whole reason Filters is instantiated a third time for this position.
    "a spell's bare subtype names cards rather than permanents" {
        statics("Zombie spells you cast cost {1} less to cast.") shouldBe listOf(
            ModifySpellCost(
                target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype(Subtype("Zombie"))),
                modification = CostModification.ReduceGeneric(1),
            )
        )
        roundTrips("Zombie spells you cast cost {1} less to cast.")
        roundTrips("Red spells you cast cost {1} less to cast.")
    }

    // Bolt Bend's sentence, read as the *gate* rather than as `FixedIfControlFilter`. The gate's
    // slot is the whole of Conditions, which is the reason it is the canonical spelling; see
    // SpellCosts' KDoc for the finding this leaves open.
    "a condition clause becomes the cost gating, not a Fixed-if source" {
        statics("This spell costs {3} less to cast if you control a creature with power 4 or greater.") shouldBe
            listOf(
                ModifySpellCost(
                    target = SpellCostTarget.SelfCast,
                    modification = CostModification.ReduceGeneric(3),
                    gating = CostGating.OnlyIf(
                        SdkConditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
                    ),
                )
            )
        roundTrips("This spell costs {3} less to cast if you control a creature with power 4 or greater.")
        roundTrips("This spell costs {1} less to cast if it's bargained.")
        roundTrips("This spell costs {2} less to cast if you control two or more creatures.")
    }

    // Grounded for Life. The one clause whose test is not a Condition — it reads the spell's own
    // target list, which CostGating.OnlyIf cannot reach, so this `FixedIf…` source stays canonical.
    "a target test stays a FixedIfAnyTargetMatches source" {
        statics("This spell costs {3} less to cast if it targets a tapped creature.") shouldBe listOf(
            ModifySpellCost(
                target = SpellCostTarget.SelfCast,
                modification = CostModification.ReduceGenericBy(
                    CostReductionSource.FixedIfAnyTargetMatches(3, GameObjectFilter.Creature.tapped())
                ),
            )
        )
        roundTrips("This spell costs {3} less to cast if it targets a tapped creature.")
        roundTrips("This spell costs {2} more to cast if it targets an artifact.")

        // A bare tribal noun is Filters' documented `alternate`: it reads correctly and prints back
        // as the adjective form, which is a VARIANT rather than a byte-exact round trip. The rule
        // under test is the target clause, so this case asserts the *model* and leaves the spelling
        // to the rule that owns it.
        statics("This spell costs {2} more to cast if it targets a Dragon.") shouldBe listOf(
            ModifySpellCost(
                target = SpellCostTarget.SelfCast,
                modification = CostModification.IncreaseGenericIfAnyTargetMatches(
                    2,
                    GameObjectFilter.Permanent.withSubtype(Subtype("Dragon")),
                ),
            )
        )
    }

    // Vanquish the Horde and The Cauldron of Eternity. The per-unit amount is spelled once, before
    // "less to cast", and only the graveyard sources can carry it — so "{2} less for each creature
    // on the battlefield" declines rather than being read as {1}.
    "the per-unit amount reaches the source that can hold it, and only that one" {
        statics("This spell costs {1} less to cast for each creature on the battlefield.") shouldBe listOf(
            ModifySpellCost(
                target = SpellCostTarget.SelfCast,
                modification = CostModification.ReduceGenericBy(
                    CostReductionSource.PermanentsOnBattlefieldMatching(GameObjectFilter.Creature)
                ),
            )
        )
        statics("This spell costs {2} less to cast for each creature card in your graveyard.") shouldBe listOf(
            ModifySpellCost(
                target = SpellCostTarget.SelfCast,
                modification = CostModification.ReduceGenericBy(
                    CostReductionSource.CardsInGraveyardMatchingFilter(GameObjectFilter.Creature, 2)
                ),
            )
        )
        roundTrips("This spell costs {1} less to cast for each creature on the battlefield.")
        roundTrips("This spell costs {2} less to cast for each creature card in your graveyard.")
        roundTrips("This spell costs {1} less to cast for each Swamp you control.")
        declines("This spell costs {2} less to cast for each creature on the battlefield.")
    }

    // "You control" is a layer of the printed noun phrase and a property of the source *type*. The
    // rule strips it on the way in, so the filter the model carries never repeats it — a filter that
    // kept it would print "for each Swamp you control you control" and re-parse to something else.
    "the you-control scope moves out of the filter and into the source" {
        val source = (
            (statics("This spell costs {1} less to cast for each Swamp you control.").single() as ModifySpellCost)
                .modification as CostModification.ReduceGenericBy
            ).source
        source shouldBe CostReductionSource.PermanentsYouControlMatching(
            GameObjectFilter.Land.withSubtype(Subtype("Swamp"))
        )
    }

    // The Lord of the Eagles and Fungal Colossus. "for each …" and ", where X is …" both build
    // ReduceGenericBy, and printing stays determined only because the two clause vocabularies take
    // disjoint source cases — so a counting source must refuse to print as a named variable.
    "a named variable takes the aggregating sources and no other" {
        statics("This spell costs {X} less to cast, where X is the total power of creatures you control.") shouldBe
            listOf(
                ModifySpellCost(
                    target = SpellCostTarget.SelfCast,
                    modification = CostModification.ReduceGenericBy(
                        CostReductionSource.TotalPropertyAmongPermanentsYouControl(
                            EntityNumericProperty.Power,
                            GameObjectFilter.Creature,
                        )
                    ),
                )
            )
        roundTrips("This spell costs {X} less to cast, where X is the total power of creatures you control.")
        roundTrips("This spell costs {X} less to cast, where X is the number of differently named lands you control.")
        // Sunderflock, whose "Elementals" is Filters' bare-noun `alternate` — a VARIANT, as above.
        statics("This spell costs {X} less to cast, where X is the greatest mana value among Elementals you control.")
            .single()
            .shouldBeInstanceOf<ModifySpellCost>()
            .modification shouldBe CostModification.ReduceGenericBy(
            CostReductionSource.GreatestPropertyAmongPermanentsYouControl(
                EntityNumericProperty.ManaValue,
                GameObjectFilter.Permanent.withSubtype(Subtype("Elemental")),
            )
        )

        val counting = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.PermanentsOnBattlefieldMatching(GameObjectFilter.Creature)
            ),
        )
        SpellCosts.all.mapNotNull { rule -> rule.unparse(counting) }
            .none { it.contains("where X is") } shouldBe true
    }

    // Temur Battlecrier and Geyser Drake. The turn clause is a wrapper over the ungated sentences
    // rather than a variant of each, and it is deliberately not a row in Conditions — if it were,
    // the trailing "if …" rule would print the same model as a second sentence.
    "the leading turn clause wraps any ungated sentence" {
        statics("During turns other than yours, spells you cast cost {1} less to cast.") shouldBe listOf(
            ModifySpellCost(
                target = SpellCostTarget.YouCast(GameObjectFilter.Any),
                modification = CostModification.ReduceGeneric(1),
                gating = CostGating.OnlyIf(SdkConditions.IsNotYourTurn),
            )
        )
        roundTrips("During turns other than yours, spells you cast cost {1} less to cast.")
        roundTrips("During your turn, this spell costs {1} less to cast for each creature on the battlefield.")
    }

    // A spell has none of a permanent's battlefield states, and the SDK gives face-down casting its
    // own subject. Reading "face-down creature spells" as a state-predicated filter would round-trip
    // byte-exactly and mean a different value — Dream Chisel, caught by the differential.
    "a battlefield state is not something a spell filter may say" {
        declines("Face-down creature spells you cast cost {1} less to cast.")
        declines("Attacking creature spells you cast cost {1} less to cast.")
    }

    // Every rule in the family can print what it parses — the meta-test each family gets, because a
    // `match` half that quietly matches nothing compiles, parses, and surfaces as a print mismatch
    // far from its cause.
    "every spell-cost rule prints what it parses" {
        val lines = listOf(
            "Noncreature spells cost {1} more to cast.",
            "Spells you cast cost {1} less to cast.",
            "Artifact spells you cast cost {1} less to cast.",
            "Cleric spells you cast cost {W}{B} less to cast.",
            "White spells you cast cost {W} more to cast.",
            "This spell costs {2} less to cast if it's bargained.",
            "This spell costs {1} less to cast if you discarded a card this turn.",
            "This spell costs {3} less to cast if it targets a tapped creature.",
            "This spell costs {2} more to cast if it targets an artifact.",
            "This spell costs {1} less to cast for each creature on the battlefield.",
            "This spell costs {1} less to cast for each Swamp you control.",
            "This spell costs {2} less to cast for each creature card in your graveyard.",
            "This spell costs {X} less to cast, where X is the total toughness of creatures you control.",
            "This spell costs {X} less to cast, where X is the number of differently named lands you control.",
            "During your turn, spells you cast cost {1} less to cast.",
            "During turns other than yours, spells you cast cost {1} less to cast.",
        )
        lines.forEach { line -> Grammar.abilityLine.printLine(fragment(line)) shouldBe line }
    }
})
