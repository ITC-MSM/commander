package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.scripting.effects.PreventDamageEffect
import com.wingedsheep.sdk.scripting.effects.PreventionDirection
import com.wingedsheep.sdk.scripting.effects.PreventionScope
import com.wingedsheep.sdk.scripting.effects.PreventionSourceFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The prevention band — one `PreventDamageEffect` and the product of its fields.
 *
 * The assertions worth keeping here are the ones about a value the printer could get wrong, not
 * about a sentence parsing:
 *
 * - each **axis** lands in its own field, so the four that vary in the sentence are the four the
 *   model carries and no combination silently collapses into another;
 * - the **source layer** owns `sourceFilter` and nothing else, which is what stops a group-sourced
 *   shield having two printed forms;
 * - the Fog value belongs to **one** sentence, because the SDK cannot tell it from a combat-only
 *   shield over the controller;
 * - a shield the sentence does not fully describe — one carrying a reaction, a life gain, or a
 *   duration other than this turn — **refuses to print** rather than dropping what it cannot say.
 */
class PreventionTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun shield(line: String): PreventDamageEffect =
        fragment(line).script.spellEffect.shouldBeInstanceOf<PreventDamageEffect>()

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    /** An alternate spelling: it parses to the same model and prints as [canonical]. */
    fun variantOf(line: String, canonical: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe canonical
        fragment(line) shouldBe fragment(canonical)
    }

    fun declines(line: String) {
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    // ---------------------------------------------------------------------------------------
    // The axes — one sentence part, one field
    // ---------------------------------------------------------------------------------------

    "the quantifier is the amount field, over three disjoint domains" {
        shield("Prevent all damage that would be dealt to you this turn.").amount shouldBe null
        shield("Prevent the next 2 damage that would be dealt to you this turn.").amount shouldBe
            DynamicAmount.Fixed(2)
        shield("Prevent the next X damage that would be dealt to you this turn.").amount shouldBe
            DynamicAmount.XValue
    }

    "the kind word is the scope field" {
        shield("Prevent all damage that would be dealt to ~ this turn.").scope shouldBe
            PreventionScope.AllDamage
        shield("Prevent all combat damage that would be dealt to ~ this turn.").scope shouldBe
            PreventionScope.CombatOnly
    }

    "the clause frame is the direction field" {
        shield("Prevent all combat damage that would be dealt to ~ this turn.").direction shouldBe
            PreventionDirection.ToTarget
        shield("Prevent all combat damage that would be dealt by ~ this turn.").direction shouldBe
            PreventionDirection.FromTarget
        shield("Prevent all combat damage that would be dealt to and dealt by ~ this turn.")
            .direction shouldBe PreventionDirection.Both
    }

    "the recipient vocabulary carries both the target and the requirement it declares" {
        fragment("Prevent all damage that would be dealt to any target this turn.")
            .script.targetRequirements.size shouldBe 1
        fragment("Prevent the next 1 damage that would be dealt to target creature this turn.")
            .script.targetRequirements.size shouldBe 1
        // "You" and the source name nothing the spell has to choose.
        fragment("Prevent all damage that would be dealt to you this turn.")
            .script.targetRequirements shouldBe emptyList()
        fragment("Prevent all damage that would be dealt to ~ this turn.")
            .script.targetRequirements shouldBe emptyList()
    }

    "the whole product round-trips, at every combination the corpus prints" {
        listOf(
            // The Fog, and the sentence it is the whole of.
            "Prevent all combat damage that would be dealt this turn.",
            // Counted shields — Aven Redeemer, Oasis, Conservator, Eiganjo Castle.
            "Prevent the next 2 damage that would be dealt to any target this turn.",
            "Prevent the next 1 damage that would be dealt to target creature this turn.",
            "Prevent the next 2 damage that would be dealt to target artifact creature this turn.",
            "Prevent the next 2 damage that would be dealt to you this turn.",
            "Prevent the next 1 damage that would be dealt to ~ this turn.",
            // Uncounted ones — Trained Pronghorn, Faithful Squire, Wellgabber Apothecary.
            "Prevent all damage that would be dealt to ~ this turn.",
            "Prevent all damage that would be dealt to target creature this turn.",
            // Silencing — Restrain, Chain of Silence, Boros Fury-Shield.
            "Prevent all combat damage that would be dealt by target attacking creature this turn.",
            "Prevent all damage target creature would deal this turn.",
            // Both directions — Deftblade Elite, Soratami Cloud Chariot.
            "Prevent all combat damage that would be dealt to and dealt by ~ this turn.",
            "Prevent all combat damage that would be dealt to and dealt by target creature you control this turn.",
            // Groups, on both sides of the damage — Inner Sanctum, Ethereal Haze, Hunter's Ambush.
            "Prevent all damage that would be dealt to creatures you control this turn.",
            "Prevent all damage that would be dealt by creatures this turn.",
            "Prevent all combat damage that would be dealt by nongreen creatures this turn.",
            // …and the recipient a GroupFilter can never hold — Eerie Interference.
            "Prevent all damage that would be dealt to you and creatures you control this turn by creatures.",
            // The source layer — Deep Wood, Consulate Surveillance, Healing Grace.
            "Prevent all damage that would be dealt to you this turn by attacking creatures.",
            "Prevent all damage that would be dealt to you this turn by a source of your choice.",
            "Prevent the next 3 damage that would be dealt to any target this turn by a source of your choice.",
        ).forEach(::roundTrips)
    }

    // ---------------------------------------------------------------------------------------
    // The source layer owns exactly one field
    // ---------------------------------------------------------------------------------------

    "the source layer sets the source and leaves the recipient alone" {
        val bare = shield("Prevent all damage that would be dealt to you this turn.")
        val chosen = shield("Prevent all damage that would be dealt to you this turn by a source of your choice.")
        chosen shouldBe bare.copy(sourceFilter = PreventionSourceFilter.ChosenSource)
    }

    // "By attacking creatures" is spellable as the dedicated case and as a FromGroup over the same
    // three words. The dedicated case is what the hand-written cards carry, so it wins and the
    // group spelling of it has no printed form — otherwise one text has two models.
    "attacking creatures is the dedicated source filter and not a group" {
        shield("Prevent all damage that would be dealt to you this turn by attacking creatures.")
            .sourceFilter shouldBe PreventionSourceFilter.AttackingCreatures
    }

    // The executor attaches an attacking-creatures shield to the ability's controller and ignores
    // `target` entirely, so the layer may only wear a recipient clause that already said "you".
    "an attacking-creatures shield refuses a recipient it could not protect" {
        declines("Prevent all damage that would be dealt to ~ this turn by attacking creatures.")
        declines("Prevent all damage that would be dealt to target creature this turn by attacking creatures.")
    }

    // A group-sourced shield is a recipient clause plus the layer, or the source frame — never both.
    "a silencing frame is not a recipient wearing the source layer" {
        shield("Prevent all damage that would be dealt by creatures this turn.").direction shouldBe
            PreventionDirection.FromTarget
        shield("Prevent all damage that would be dealt to you this turn by creatures.").direction shouldBe
            PreventionDirection.ToTarget
        fragment("Prevent all damage that would be dealt by creatures this turn.") shouldNotBe
            fragment("Prevent all damage that would be dealt to you this turn by creatures.")
    }

    // ---------------------------------------------------------------------------------------
    // The Fog collision — one model, and therefore one sentence
    // ---------------------------------------------------------------------------------------

    // `PreventDamageEffect(scope = CombatOnly)` leaves `target` at its Controller default, and
    // PreventDamageExecutor reads exactly that as *global* prevention. So the narrower English has
    // nowhere to land and declines rather than round-tripping as something else.
    "a combat-only shield over the controller is the Fog and nothing else can print it" {
        shield("Prevent all combat damage that would be dealt this turn.") shouldBe
            PreventDamageEffect(scope = PreventionScope.CombatOnly)
        declines("Prevent all combat damage that would be dealt to you this turn.")
        // The all-damage sibling is a real shield on the controller and stays readable.
        roundTrips("Prevent all damage that would be dealt to you this turn.")
    }

    // ---------------------------------------------------------------------------------------
    // The canonical order flips with the scope
    // ---------------------------------------------------------------------------------------

    // Oracle spells `FromTarget` two ways and which one is the majority flips with the kind of
    // damage: the passive leads 11 lines to 3 for combat damage, and the active is the only
    // spelling the corpus uses for damage in general. Each rule takes the half it wins.
    "each kind of damage prints the silencing frame the corpus prints for it" {
        variantOf(
            "Prevent all combat damage target creature would deal this turn.",
            "Prevent all combat damage that would be dealt by target creature this turn.",
        )
        variantOf(
            "Prevent all damage that would be dealt by target creature this turn.",
            "Prevent all damage target creature would deal this turn.",
        )
    }

    "the duration and the group swap places in both directions and print one way" {
        variantOf(
            "Prevent all damage that would be dealt this turn to creatures you control.",
            "Prevent all damage that would be dealt to creatures you control this turn.",
        )
        variantOf(
            "Prevent all combat damage that would be dealt this turn by creatures with flying.",
            "Prevent all combat damage that would be dealt by creatures with flying this turn.",
        )
    }

    // ---------------------------------------------------------------------------------------
    // Fail-closed: a shield the sentence cannot describe refuses to print
    // ---------------------------------------------------------------------------------------

    "a shield carrying a field no rule spells has no printed form" {
        val bare = fragment("Prevent all damage that would be dealt to you this turn.")
        fun printing(effect: PreventDamageEffect) =
            Grammar.abilityLine.printLine(bare.copy(script = bare.script.copy(spellEffect = effect)))

        val shield = bare.script.spellEffect as PreventDamageEffect
        printing(shield) shouldNotBe null
        // A reaction, a life gain and a single-instance flag are all things this English does not say.
        printing(shield.copy(gainLifeFromColors = setOf(com.wingedsheep.sdk.core.Color.BLACK))) shouldBe null
        printing(shield.copy(nextInstanceOnly = true)) shouldBe null
        printing(shield.copy(preventDamage = false)) shouldBe null
        printing(shield.copy(duration = com.wingedsheep.sdk.scripting.Duration.UntilYourNextTurn)) shouldBe null
    }

    // ---------------------------------------------------------------------------------------
    // The anaphor, and the position it is readable from
    // ---------------------------------------------------------------------------------------

    // "That creature" is the target an earlier clause chose, so the shield declares no requirement
    // of its own — and a line whose whole content is that dangling reference is not a card.
    "the anaphoric shield is readable only after a clause that introduced a target" {
        val line = "Untap target creature. Prevent all combat damage that would be dealt to and " +
            "dealt by that creature this turn."
        roundTrips(line)
        declines("Prevent all combat damage that would be dealt to and dealt by that creature this turn.")
    }

    "the pronoun parses and the noun prints" {
        variantOf(
            "Untap target creature. Prevent all combat damage that would be dealt to it this turn.",
            "Untap target creature. Prevent all combat damage that would be dealt to that creature this turn.",
        )
    }

    // ---------------------------------------------------------------------------------------
    // Two SDK gaps this band reports rather than approximates
    // ---------------------------------------------------------------------------------------

    // PreventionScope has AllDamage and CombatOnly and no Noncombat, and reading the third as the
    // first would be the reversible-but-wrong class in one word.
    "noncombat damage has no scope to land in" {
        declines("Prevent all noncombat damage that would be dealt to other creatures you control this turn.")
    }

    // A GroupFilter names permanents, so "players" is a recipient set the field cannot hold.
    "a recipient group of players is not expressible" {
        declines("Prevent all combat damage that would be dealt to players this turn.")
    }

    "every prevention rule prints what it parses" {
        listOf(
            "Prevent all combat damage that would be dealt this turn.",
            "Prevent all damage that would be dealt to you this turn.",
            "Prevent all damage that would be dealt to ~ this turn.",
            "Prevent all damage that would be dealt to any target this turn.",
            "Prevent all damage that would be dealt to target creature this turn.",
            "Prevent the next 1 damage that would be dealt to any target this turn.",
            "Prevent the next X damage that would be dealt to target creature this turn.",
            "Prevent all damage target creature would deal this turn.",
            "Prevent all combat damage that would be dealt by target creature this turn.",
            "Prevent all combat damage that would be dealt to and dealt by ~ this turn.",
            "Prevent all damage that would be dealt to creatures you control this turn.",
            "Prevent all damage that would be dealt to you and creatures you control this turn.",
            "Prevent all damage that would be dealt by creatures this turn.",
            "Prevent all damage that would be dealt to you this turn by attacking creatures.",
            "Prevent all damage that would be dealt to you this turn by a source of your choice.",
            "Prevent all damage that would be dealt to you this turn by creatures.",
        ).forEach { line -> Grammar.abilityLine.printLine(fragment(line)) shouldBe line }
    }
})
