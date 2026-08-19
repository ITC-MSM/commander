package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheStashedSkeleton
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import com.wingedsheep.engine.core.SelectCardsDecision
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Case of the Stashed Skeleton — {1}{B} Enchantment — Case.
 *
 * The Case makes its own obstacle: the suspected Skeleton it creates is exactly what keeps
 * "you control no suspected Skeletons" false. Two things to prove — the suspect lands on the token
 * this resolution created (not on some other Skeleton), and the Case stays unsolved until that
 * token is gone.
 */
class CaseOfTheStashedSkeletonScenarioTest : FunSpec({

    /** {1} sorcery: destroy target creature — the way to get rid of the Skeleton on cue. */
    val testExecute = card("Test Execute") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        oracleText = "Destroy target creature."
        spell {
            val t = target(
                "target",
                com.wingedsheep.sdk.scripting.targets.TargetCreature(
                    filter = com.wingedsheep.sdk.scripting.filters.unified.TargetFilter.Creature
                )
            )
            effect = com.wingedsheep.sdk.dsl.Effects.Destroy(t)
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaseOfTheStashedSkeleton)
        driver.registerCard(testExecute)
        driver.initMirrorMatch(Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    /** Cast the Case and resolve its enters trigger. */
    fun GameTestDriver.playCase(): EntityId {
        val card = putCardInHand(player1, "Case of the Stashed Skeleton")
        giveMana(player1, Color.BLACK, 2)
        castSpell(player1, card).isSuccess shouldBe true
        bothPass()
        bothPass()
        return card
    }

    fun GameTestDriver.skeleton(): EntityId =
        getPermanents(player1).single { getCardName(it) == "Skeleton" }

    test("the enters trigger makes a 2/1 black Skeleton and suspects that token") {
        val driver = newDriver()
        driver.playCase()

        val token = driver.skeleton()
        val projected = driver.state.projectedState
        projected.getPower(token) shouldBe 2
        projected.getToughness(token) shouldBe 1
        projected.isSuspected(token) shouldBe true
        // Suspect's two riders come with it (CR 701.60a).
        projected.hasKeyword(token, Keyword.MENACE) shouldBe true
    }

    test("the suspected Skeleton keeps it unsolved until the Skeleton is gone") {
        val driver = newDriver()
        val case = driver.playCase()
        val token = driver.skeleton()

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val removal = driver.putCardInHand(driver.player1, "Test Execute")
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, removal, listOf(token)).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getBattlefield().contains(token) shouldBe false

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
    }

    test("Solved — the tutor puts any card from your library into your hand") {
        val driver = newDriver()
        val case = driver.playCase()
        val token = driver.skeleton()

        val removal = driver.putCardInHand(driver.player1, "Test Execute")
        driver.giveColorlessMana(driver.player1, 1)
        driver.castSpell(driver.player1, removal, listOf(token)).isSuccess shouldBe true
        driver.bothPass()

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val handBefore = driver.getHandSize(driver.player1)
        driver.giveMana(driver.player1, Color.BLACK, 2)
        val ability = CaseOfTheStashedSkeleton.activatedAbilities.first().id
        driver.submitSuccess(ActivateAbility(driver.player1, case, ability))
        driver.bothPass()

        // The search is unrestricted — every card in the library is an option.
        val decision = driver.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        driver.submitCardSelection(driver.player1, listOf(decision.options.first())).error shouldBe null

        driver.getHandSize(driver.player1) shouldBe handBefore + 1
        driver.state.getBattlefield().contains(case) shouldBe false
    }
})
