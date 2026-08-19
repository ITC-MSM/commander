package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.SolvedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CaseOfTheUneatenFeast
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Case of the Uneaten Feast — {W} Enchantment — Case.
 *
 * The Solved ability is the part that needed new engine plumbing, and its rules edge is CR 611.2c:
 * the permission is granted to the *cards that are in the graveyard when the ability resolves*, so
 * a creature card that arrives later the same turn is not covered. These tests pin that, the
 * creature-cards-only scope, and the end-of-turn expiry — plus the life trigger and the
 * amount-not-event-count reading of "you've gained 5 or more life this turn".
 */
class CaseOfTheUneatenFeastScenarioTest : FunSpec({

    /** A {W} 1/1 to enter the battlefield and to sit in the graveyard as a castable target. */
    val testDog = card("Feast Dog") {
        manaCost = "{W}"
        typeLine = "Creature — Dog"
        power = 1
        toughness = 1
        oracleText = ""
    }

    /** A second creature card for the graveyard, so the grant lands on more than one card. */
    val testOx = card("Feast Ox") {
        manaCost = "{W}"
        typeLine = "Creature — Ox"
        power = 2
        toughness = 2
        oracleText = ""
    }

    /** A {W} instant that gains five life in a single event — five points, one gain. */
    val testBlessing = card("Feast Blessing") {
        manaCost = "{W}"
        typeLine = "Instant"
        oracleText = "You gain 5 life."
        spell { effect = Effects.GainLife(5) }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(CaseOfTheUneatenFeast)
        driver.registerCard(testDog)
        driver.registerCard(testOx)
        driver.registerCard(testBlessing)
        driver.initMirrorMatch(Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.isSolved(id: EntityId): Boolean =
        state.getEntity(id)?.has<SolvedComponent>() == true

    /** Cast a Feast Dog from hand and let it resolve. */
    fun GameTestDriver.castDog() {
        val dog = putCardInHand(player1, "Feast Dog")
        giveMana(player1, Color.WHITE, 1)
        castSpell(player1, dog).isSuccess shouldBe true
        bothPass() // the Dog resolves; the Case's life trigger goes on the stack
        bothPass() // resolve the life trigger
    }

    /** Names of the cards this player may currently cast out of their graveyard. */
    fun GameTestDriver.graveyardCastNames(): List<String> =
        legalActions(player1)
            .filter { it.sourceZone == "GRAVEYARD" }
            .map { it.description }

    test("a creature you control entering gains you 1 life") {
        val driver = newDriver()
        driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")
        val before = driver.getLifeTotal(driver.player1)

        driver.castDog()

        driver.getLifeTotal(driver.player1) shouldBe before + 1
    }

    test("four points isn't enough, and the count doesn't carry over to the next turn") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")

        // Four creatures is four life: not enough.
        repeat(4) { driver.castDog() }
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false

        // The tracker resets each turn, so those four points are gone: a single Dog next turn is
        // one point, not a fifth, and the Case stays unsolved.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.castDog()
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe false
    }

    test("five gains of one solve it — five separate creatures each gaining 1 life") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")

        repeat(5) { driver.castDog() }
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
    }

    test("one gain of five solves it too — the amount, not the number of gains") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")

        // A single life-gain event worth five points. If the tracker counted gain *events* rather
        // than life, this one would read 1 and the Case would never solve.
        val blessing = driver.putCardInHand(driver.player1, "Feast Blessing")
        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.castSpell(driver.player1, blessing).isSuccess shouldBe true
        driver.bothPass()

        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true
    }

    test("Solved — sacrificing it lets creature cards already in your graveyard be cast from there") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")
        val dogInYard = driver.putCardInGraveyard(driver.player1, "Feast Dog")

        repeat(5) { driver.castDog() }
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.graveyardCastNames().none { it.contains("Feast Dog") } shouldBe true

        val ability = CaseOfTheUneatenFeast.activatedAbilities.first().id
        driver.submitSuccess(ActivateAbility(driver.player1, case, ability))
        driver.bothPass() // resolve the grant

        driver.state.getBattlefield().contains(case) shouldBe false
        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.graveyardCastNames().any { it.contains("Feast Dog") } shouldBe true

        // And it really is castable: cast it and it comes back as a permanent.
        driver.castSpell(driver.player1, dogInYard).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getBattlefield().contains(dogInYard) shouldBe true
    }

    test("a creature card that reaches the graveyard afterwards is not covered (CR 611.2c)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")

        repeat(5) { driver.castDog() }
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ability = CaseOfTheUneatenFeast.activatedAbilities.first().id
        driver.submitSuccess(ActivateAbility(driver.player1, case, ability))
        driver.bothPass()

        // The graveyard was empty of creature cards when the ability resolved.
        val latecomer = driver.putCardInGraveyard(driver.player1, "Feast Dog")
        driver.giveMana(driver.player1, Color.WHITE, 1)

        driver.legalActions(driver.player1)
            .none { it.sourceZone == "GRAVEYARD" && it.action.let { a -> a is com.wingedsheep.engine.core.CastSpell && a.cardId == latecomer } } shouldBe true
    }

    /** Solve the Case, move to a later turn, and sacrifice it for the graveyard-cast grant. */
    fun GameTestDriver.solveAndActivate(case: EntityId) {
        repeat(5) { castDog() }
        passPriorityUntil(Step.END)
        bothPass()
        isSolved(case) shouldBe true

        passPriorityUntil(Step.UPKEEP)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
        passPriorityUntil(Step.UPKEEP)
        passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ability = CaseOfTheUneatenFeast.activatedAbilities.first().id
        submitSuccess(ActivateAbility(player1, case, ability))
        bothPass()
    }

    test("a card cast this way that dies is a new object and isn't castable again (CR 400.7)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")
        val dogInYard = driver.putCardInGraveyard(driver.player1, "Feast Dog")
        // A second creature card in the yard holds a grant of its own. That grant is scoped to the
        // Ox — it must not read as a standing "cast creature cards from your graveyard" permission
        // that keeps the Dog castable after the Dog's own grant is gone.
        driver.putCardInGraveyard(driver.player1, "Feast Ox")

        driver.solveAndActivate(case)

        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.castSpell(driver.player1, dogInYard).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getBattlefield().contains(dogInYard) shouldBe true

        // Kill it. The card that lands back in the graveyard is a new object, so the permission the
        // Case handed to the old object does not follow it.
        val bolt = driver.putCardInHand(driver.player1, "Lightning Bolt")
        driver.giveMana(driver.player1, Color.RED, 1)
        driver.castSpell(driver.player1, bolt, listOf(dogInYard)).isSuccess shouldBe true
        driver.bothPass()
        driver.getGraveyard(driver.player1).contains(dogInYard) shouldBe true

        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.graveyardCastNames().none { it.contains("Feast Dog") } shouldBe true
        // The Ox's own grant is untouched — only the Dog's incarnation changed.
        driver.graveyardCastNames().any { it.contains("Feast Ox") } shouldBe true
    }

    test("a countered graveyard cast doesn't leave the card castable again (CR 400.7)") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")
        val dogInYard = driver.putCardInGraveyard(driver.player1, "Feast Dog")

        driver.solveAndActivate(case)

        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.castSpell(driver.player1, dogInYard).isSuccess shouldBe true

        // The Dog left the graveyard when it was cast, so it stopped being the object the Case
        // granted the permission to — being countered back into the yard makes a third object.
        val counter = driver.putCardInHand(driver.player2, "Counterspell")
        driver.giveMana(driver.player2, Color.BLUE, 2)
        driver.passPriority(driver.player1) // hand priority to the opponent with the Dog on the stack
        driver.castSpellWithTargets(
            driver.player2, counter, listOf(ChosenTarget.Spell(dogInYard))
        ).isSuccess shouldBe true
        driver.bothPass() // Counterspell resolves, countering the Dog
        driver.getGraveyard(driver.player1).contains(dogInYard) shouldBe true

        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.graveyardCastNames().none { it.contains("Feast Dog") } shouldBe true
    }

    test("the permission wears off at end of turn") {
        val driver = newDriver()
        val case = driver.putPermanentOnBattlefield(driver.player1, "Case of the Uneaten Feast")
        driver.putCardInGraveyard(driver.player1, "Feast Dog")

        repeat(5) { driver.castDog() }
        driver.passPriorityUntil(Step.END)
        driver.bothPass()
        driver.isSolved(case) shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val ability = CaseOfTheUneatenFeast.activatedAbilities.first().id
        driver.submitSuccess(ActivateAbility(driver.player1, case, ability))
        driver.bothPass()
        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.graveyardCastNames().any { it.contains("Feast Dog") } shouldBe true

        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.giveMana(driver.player1, Color.WHITE, 1)
        driver.graveyardCastNames().none { it.contains("Feast Dog") } shouldBe true
    }
})
