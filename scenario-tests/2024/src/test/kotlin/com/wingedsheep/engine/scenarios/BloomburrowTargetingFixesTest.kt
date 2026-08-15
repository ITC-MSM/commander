package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.blb.cards.MabelsMettle
import com.wingedsheep.mtg.sets.definitions.blb.cards.SkyskipperDuo
import com.wingedsheep.mtg.sets.definitions.blb.cards.StormchasersTalent
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Targeting regression guards for three BLB oracle-text fixes:
 *
 * - Skyskipper Duo: "exile up to one OTHER target creature you control" — it must not
 *   be able to blink itself.
 * - Stormchaser's Talent level 2: "return target instant or sorcery card FROM YOUR
 *   GRAVEYARD" — opponents' graveyards are off-limits.
 * - Mabel's Mettle: "Up to one OTHER target creature gets +1/+1" — the second target
 *   must differ from the first (no stacking +3/+3 on one creature).
 */
class BloomburrowTargetingFixesTest : FunSpec({

    // Triggered-ability targets are chosen when the trigger is put on the stack, so the
    // ChooseTargetsDecision can surface before or after a priority pass — pass until it shows.
    fun GameTestDriver.passUntilDecision(max: Int = 4) {
        repeat(max) {
            if (pendingDecision != null || state.priorityPlayerId == null) return
            bothPass()
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(SkyskipperDuo, StormchasersTalent, MabelsMettle))
        return driver
    }

    test("Skyskipper Duo's ETB blink cannot target itself") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val lions = driver.putCreatureOnBattlefield(active, "Savannah Lions")

        driver.giveMana(active, Color.BLUE, 5)
        val duo = driver.putCardInHand(active, "Skyskipper Duo")
        driver.castSpell(active, duo)
        driver.passUntilDecision() // resolve the creature spell; the ETB trigger asks for a target

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<ChooseTargetsDecision>()
        val skyskipper = driver.findPermanent(active, "Skyskipper Duo")!!
        val legal = decision.legalTargets[0].orEmpty()
        legal shouldContain lions
        legal shouldNotContain skyskipper
    }

    test("Stormchaser's Talent level 2 can only return cards from YOUR graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val mine = driver.putCardInGraveyard(active, "Lightning Bolt")
        val theirs = driver.putCardInGraveyard(opponent, "Lightning Bolt")

        // Materialize the Class already at level 2 — the "becomes level 2" trigger fires.
        driver.giveMana(active, Color.BLUE, 4)
        val talent = driver.putCardInHand(active, "Stormchaser's Talent")
        driver.castSpell(active, talent)
        driver.bothPass() // resolve the enchantment; its ETB token trigger goes on the stack
        if (driver.state.stack.isNotEmpty()) driver.bothPass() // resolve the level-1 token trigger

        val classId = driver.findPermanent(active, "Stormchaser's Talent")!!
        driver.giveMana(active, Color.BLUE, 4)
        driver.submitSuccess(
            com.wingedsheep.engine.core.ActivateAbility(
                playerId = active,
                sourceId = classId,
                abilityId = com.wingedsheep.sdk.scripting.AbilityId.classLevelUp(2)
            )
        )
        driver.passUntilDecision() // resolve the level-up → the becomes-level-2 trigger targets

        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<ChooseTargetsDecision>()
        val legal = decision.legalTargets[0].orEmpty()
        legal shouldContain mine
        legal shouldNotContain theirs
    }

    test("Mabel's Mettle cannot choose the same creature for both targets") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val lions = driver.putCreatureOnBattlefield(active, "Savannah Lions")
        val bear = driver.putCreatureOnBattlefield(active, "Centaur Courser")

        // Same creature for both target slots must be rejected outright.
        driver.giveMana(active, Color.WHITE, 2)
        val mettle = driver.putCardInHand(active, "Mabel's Mettle")
        val sameTwice = driver.castSpellWithTargets(
            active, mettle,
            listOf(ChosenTarget.Permanent(lions), ChosenTarget.Permanent(lions))
        )
        sameTwice.isSuccess shouldBe false

        // Two different creatures is legal.
        val differing = driver.castSpellWithTargets(
            active, mettle,
            listOf(ChosenTarget.Permanent(lions), ChosenTarget.Permanent(bear))
        )
        differing.isSuccess shouldBe true
    }
})
