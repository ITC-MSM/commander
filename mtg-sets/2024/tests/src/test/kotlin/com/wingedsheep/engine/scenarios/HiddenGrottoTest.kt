package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PaymentStrategy
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.HiddenGrotto
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.CreatureStats
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.state.components.identity.CardComponent
import java.util.UUID

/**
 * Tests for Hidden Grotto.
 *
 * Hidden Grotto
 * Land
 * When this land enters, surveil 1.
 * {T}: Add {C}.
 * {1}, {T}: Add one mana of any color.
 *
 * Regression: the {1}, {T} ability must not be payable by tapping Hidden Grotto
 * itself to produce the {1}. The outer tap cost already consumes the single
 * "tap" available on the permanent, so its own mana ability cannot contribute.
 */
class HiddenGrottoTest : FunSpec({

    val anyColorAbilityId = HiddenGrotto.activatedAbilities[1].id

    // A creature with a non-mana {G}{G} activated pump ability. Activating it auto-taps for
    // {G}{G}; with only one Forest available, the solver routes the second {G} through Hidden
    // Grotto's any-color ability, which itself costs {1} — funded by tapping a basic. That
    // basic lands in solution.sources with no manaProduced entry (its mana is consumed by the
    // activation cost), which previously made autoTapForManaCost throw.
    val pumpAbilityId = AbilityId(UUID.randomUUID().toString())
    val grottoBeast = CardDefinition(
        name = "Grotto Beast",
        manaCost = ManaCost.parse("{2}{G}"),
        typeLine = TypeLine.creature(setOf(Subtype("Beast"))),
        creatureStats = CreatureStats(2, 2),
        script = CardScript.permanent(
            ActivatedAbility(
                id = pumpAbilityId,
                cost = Costs.Mana(ManaCost.parse("{G}{G}")),
                effect = Effects.ModifyStats(1, 1, EffectTarget.Self),
            )
        )
    )

    test("cannot activate {1}, {T}: Add one mana of any color using its own mana ability") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Plains" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val grotto = driver.putPermanentOnBattlefield(activePlayer, "Hidden Grotto")

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = grotto,
                abilityId = anyColorAbilityId
            )
        )

        result.isSuccess shouldBe false
        driver.isTapped(grotto) shouldBe false
    }

    test("{1}, {T}: Add one mana of any color taps another land for the {1}") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Plains" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val grotto = driver.putPermanentOnBattlefield(activePlayer, "Hidden Grotto")
        val mountain = driver.putPermanentOnBattlefield(activePlayer, "Mountain")

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = grotto,
                abilityId = anyColorAbilityId,
                manaColorChoice = Color.GREEN
            )
        )

        result.isSuccess shouldBe true
        // Both Hidden Grotto (outer tap cost) and Mountain (paying the {1}) must be tapped.
        // Bug: solver picks Grotto's own {T}: Add {C} to cover the {1}, leaving Mountain untapped.
        driver.isTapped(grotto) shouldBe true
        driver.isTapped(mountain) shouldBe true
    }

    test("auto-tap for {G}{G} with Swamp + Forest + Hidden Grotto taps the swamp to fund Grotto's {1}") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Plains" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val swamp = driver.putPermanentOnBattlefield(activePlayer, "Swamp")
        val forest = driver.putPermanentOnBattlefield(activePlayer, "Forest")
        val grotto = driver.putPermanentOnBattlefield(activePlayer, "Hidden Grotto")
        val curator = driver.putCardInHand(activePlayer, "Keen-Eyed Curator")

        val result = driver.castSpell(activePlayer, curator)

        result.isSuccess shouldBe true
        // Forest pays one {G}; Hidden Grotto's any-color ability pays the other {G}
        // but requires {1} — which must come from tapping the Swamp.
        driver.isTapped(forest) shouldBe true
        driver.isTapped(grotto) shouldBe true
        driver.isTapped(swamp) shouldBe true
    }

    test("activating an ability auto-taps a basic to fund Grotto's {1} without crashing") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(grottoBeast))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Plains" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val forest = driver.putPermanentOnBattlefield(activePlayer, "Forest")
        val grotto = driver.putPermanentOnBattlefield(activePlayer, "Hidden Grotto")
        val swamp = driver.putPermanentOnBattlefield(activePlayer, "Swamp")
        val beast = driver.putPermanentOnBattlefield(activePlayer, "Grotto Beast")

        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = beast,
                abilityId = pumpAbilityId
            )
        )

        // {G}{G}: Forest pays one {G}; Hidden Grotto's any-color ability pays the other {G},
        // and its own {1} is funded by tapping the Swamp.
        result.isSuccess shouldBe true
        driver.isTapped(forest) shouldBe true
        driver.isTapped(grotto) shouldBe true
        driver.isTapped(swamp) shouldBe true
    }

    test("cannot use its own mana ability to pay the {1} via explicit payment") {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Plains" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val grotto = driver.putPermanentOnBattlefield(activePlayer, "Hidden Grotto")

        // Client explicitly requests that Grotto's own mana ability pay for the {1}.
        // The engine must refuse — the outer ability's Tap cost precludes also using
        // the same permanent as a mana source.
        val result = driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = grotto,
                abilityId = anyColorAbilityId,
                paymentStrategy = PaymentStrategy.Explicit(listOf(grotto))
            )
        )

        result.isSuccess shouldBe false
        driver.isTapped(grotto) shouldBe false
    }
})
