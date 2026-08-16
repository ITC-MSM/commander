package com.wingedsheep.assay.grammar

import com.wingedsheep.assay.syntax.ParseOutcome
import com.wingedsheep.assay.syntax.parseLine
import com.wingedsheep.assay.syntax.printLine
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.dsl.Triggers as SdkTriggers
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * The spell-cast triggers and the [Spells] noun phrase they slot — the band that made a *spell* a
 * thing the grammar can describe, rather than only a permanent.
 */
class SpellCastTriggersTest : StringSpec({

    fun fragment(line: String): CardFragment =
        Grammar.abilityLine.parseLine(line).shouldBeInstanceOf<ParseOutcome.Accepted<CardFragment>>().value

    fun roundTrips(line: String) {
        Grammar.abilityLine.printLine(fragment(line)) shouldBe line
    }

    fun ability(line: String): TriggeredAbility =
        fragment(line).script.triggeredAbilities.single()

    // Spellgorger Weird's golden is this model exactly.
    "a cast trigger is the ability a card author writes from the same sentence" {
        ability("Whenever you cast a noncreature spell, put a +1/+1 counter on ~.") shouldBe
            TriggeredAbility(
                id = AbilityId("trigger"),
                trigger = EventPattern.SpellCastEvent(
                    spellFilter = GameObjectFilter.Noncreature,
                    player = Player.You,
                ),
                binding = SdkTriggers.YouCastNoncreature.binding,
                effect = Effects.AddCounters("+1/+1", 1, EffectTarget.Self),
            )
        roundTrips("Whenever you cast a noncreature spell, put a +1/+1 counter on ~.")
    }

    "the caster is a field on the event, so the three subjects are three rows" {
        (ability("Whenever you cast a spell, draw a card.").trigger as EventPattern.SpellCastEvent)
            .player shouldBe Player.You
        (ability("Whenever an opponent casts a spell, draw a card.").trigger as EventPattern.SpellCastEvent)
            .player shouldBe Player.EachOpponent
        (ability("Whenever a player casts a spell, draw a card.").trigger as EventPattern.SpellCastEvent)
            .player shouldBe Player.Each
    }

    "the effect clause is the whole step vocabulary, exactly as every other trigger's is" {
        listOf(
            "Whenever you cast a spell, draw a card.",
            "Whenever you cast a creature spell, scry 2.",
            "Whenever you cast an artifact spell, destroy target creature.",
            "Whenever an opponent casts an enchantment spell, you gain 2 life.",
            "Whenever a player casts a red spell, tap target creature.",
        ).forEach { roundTrips(it) }
    }

    // The noun phrase is layered like [Filters]', over the three axes a card carries on the stack.
    "the spell noun phrase layers type, subtype, colour and mana value" {
        listOf(
            "Whenever you cast a spell, draw a card.",
            "Whenever you cast a noncreature spell, draw a card.",
            "Whenever you cast an instant or sorcery spell, draw a card.",
            "Whenever you cast a historic spell, draw a card.",
            "Whenever you cast a multicolored spell, draw a card.",
            "Whenever you cast a legendary spell, draw a card.",
            "Whenever you cast a permanent spell, draw a card.",
            "Whenever you cast a blue spell, draw a card.",
            "Whenever you cast a Merfolk spell, draw a card.",
            "Whenever you cast a Spirit or Arcane spell, draw a card.",
            "Whenever you cast a spell with mana value 4 or greater, draw a card.",
            "Whenever you cast a creature spell with mana value 5 or greater, draw a card.",
        ).forEach { roundTrips(it) }
    }

    "the article is derived from the printed noun, in both directions" {
        roundTrips("Whenever you cast an artifact spell, draw a card.")
        roundTrips("Whenever you cast an enchantment spell, draw a card.")
        roundTrips("Whenever you cast a creature spell, draw a card.")
        // "an creature spell" is not English and must not parse either, which is what makes the
        // article a function of the phrase rather than a choice the printer makes.
        Grammar.abilityLine.parseLine("Whenever you cast an creature spell, draw a card.")
            .shouldBeInstanceOf<ParseOutcome.Declined>()
    }

    // Storyteller Pixie. The reading round-tripped byte-perfectly as `Any.withSubtype(Adventure)`
    // and meant a card the engine would never fire; the differential is what caught it.
    "an Adventure spell is a cast-manner fact, not a subtype on the object" {
        ability("Whenever you cast an Adventure spell, draw a card.").trigger shouldBe
            EventPattern.SpellCastEvent(requires = setOf(SpellCastPredicate.CastAsAdventure))
        roundTrips("Whenever you cast an Adventure spell, draw a card.")

        // …and the subtype leaf must refuse the word, or one printed form would have two models.
        Spells.spell.unparse(GameObjectFilter.Any.withSubtype(Subtype("Adventure"))) shouldBe null
    }

    "a cast trigger on the spell itself is a different event and keeps the source anaphor" {
        ability("When you cast this spell, draw a card.").trigger shouldBe
            EventPattern.CastThisSpellEvent
        roundTrips("When you cast this spell, draw a card.")
    }

    "the ordinal cast trigger counts a caster's spells within the turn" {
        ability("Whenever you cast your second spell each turn, draw a card.").trigger shouldBe
            EventPattern.NthSpellCastEvent(nthSpell = 2, player = Player.You)
        listOf(
            "Whenever you cast your first spell each turn, draw a card.",
            "Whenever you cast your second spell each turn, draw a card.",
            "Whenever you cast your third spell each turn, draw a card.",
            "Whenever a player casts their second spell each turn, draw a card.",
            "Whenever a player casts their first noncreature spell each turn, draw a card.",
        ).forEach { roundTrips(it) }
    }

    // The SDK spells "count every spell" as a null filter here and as `Any` on `SpellCastEvent`;
    // every hand-written ordinal trigger writes the null, so `Any` must not print at all.
    "an ordinal event carrying Any rather than null refuses to print" {
        val script = CardScript(
            triggeredAbilities = listOf(
                TriggeredAbility(
                    id = AbilityId("trigger"),
                    trigger = EventPattern.NthSpellCastEvent(
                        nthSpell = 2,
                        player = Player.You,
                        spellFilter = GameObjectFilter.Any,
                    ),
                    binding = SdkTriggers.YouCastSpell.binding,
                    effect = Effects.DrawCards(1),
                ),
            ),
        )
        Grammar.abilityLine.printLine(CardFragment(script = script)) shouldBe null
    }

    // Guards the one failure mode a per-rule test cannot: a `match` half that quietly matches
    // nothing, which surfaces on the corpus as a print mismatch far from its cause.
    "every spell-cast rule can print what it parses" {
        listOf(
            "Whenever you cast a spell, draw a card.",
            "Whenever you cast a creature spell, draw a card.",
            "Whenever you cast a noncreature spell, draw a card.",
            "Whenever you cast an instant or sorcery spell, draw a card.",
            "Whenever you cast an artifact spell, draw a card.",
            "Whenever you cast an enchantment spell, draw a card.",
            "Whenever you cast a planeswalker spell, draw a card.",
            "Whenever you cast a permanent spell, draw a card.",
            "Whenever you cast an instant spell, draw a card.",
            "Whenever you cast a sorcery spell, draw a card.",
            "Whenever you cast a historic spell, draw a card.",
            "Whenever you cast a multicolored spell, draw a card.",
            "Whenever you cast a legendary spell, draw a card.",
            "Whenever you cast a white spell, draw a card.",
            "Whenever you cast a Knight spell, draw a card.",
            "Whenever you cast an Aura or Equipment spell, draw a card.",
            "Whenever you cast a spell with mana value 3 or greater, draw a card.",
            "Whenever you cast an Adventure spell, draw a card.",
            "Whenever an opponent casts a spell, draw a card.",
            "Whenever an opponent casts a noncreature spell, draw a card.",
            "Whenever a player casts a spell, draw a card.",
            "Whenever a player casts a black spell, draw a card.",
            "When you cast this spell, draw a card.",
            "Whenever you cast your second spell each turn, draw a card.",
            "Whenever a player casts their first spell each turn, draw a card.",
        ).forEach { roundTrips(it) }
    }
})
