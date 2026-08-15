package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.khc.cards.LathrilBladeOfTheElves
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Lathril, Blade of the Elves (KHC #1).
 *
 * {2}{B}{G} Legendary Creature — Elf Noble 2/3, Menace
 *  Whenever Lathril deals combat damage to a player, create that many 1/1 green Elf Warrior
 *   creature tokens.
 *  {T}, Tap ten untapped Elves you control: Each opponent loses 10 life and you gain 10 life.
 *
 * The interesting bit is `excludeSelf = true` on the tap-ten cost: per the KHC ruling Lathril
 * doesn't count as one of the ten, so nine other Elves is not enough.
 */
class LathrilBladeOfTheElvesScenarioTest : FunSpec({

    val drainAbilityId = LathrilBladeOfTheElves.activatedAbilities[0].id

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Put Lathril onto the battlefield ready to act, plus [otherElves] other untapped Elves. */
    fun setUpElves(driver: GameTestDriver, player: EntityId, otherElves: Int): Pair<EntityId, List<EntityId>> {
        val lathril = driver.putCreatureOnBattlefield(player, "Lathril, Blade of the Elves")
        driver.removeSummoningSickness(lathril)
        val elves = (1..otherElves).map {
            val elf = driver.putCreatureOnBattlefield(player, "Elvish Warrior") // 2/3 Elf Warrior
            driver.removeSummoningSickness(elf)
            elf
        }
        return lathril to elves
    }

    test("combat damage to a player creates that many 1/1 green Elf Warrior tokens") {
        val driver = createDriver()
        val player = driver.player1
        val opponent = driver.player2

        val lathril = driver.putCreatureOnBattlefield(player, "Lathril, Blade of the Elves")
        driver.removeSummoningSickness(lathril)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(lathril), opponent)
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.passPriorityUntil(Step.COMBAT_DAMAGE)
        if (driver.pendingDecision is CombatResolutionDecision) driver.confirmCombatDamage()
        driver.bothPass()

        withClue("Lathril's 2 power dealt 2 damage") { driver.getLifeTotal(opponent) shouldBe 18 }

        val tokens = driver.getPermanents(player).filter { driver.getCardName(it) == "Elf Warrior Token" }
        withClue("two damage = two Elf Warrior tokens") { tokens.size shouldBe 2 }
    }

    test("tap ten OTHER untapped Elves: each opponent loses 10 life and you gain 10") {
        val driver = createDriver()
        val player = driver.player1
        val opponent = driver.player2

        val (lathril, elves) = setUpElves(driver, player, otherElves = 10)

        val activation = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = lathril,
                abilityId = drainAbilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = elves)
            )
        )
        withClue("activating with ten other Elves should be allowed: ${activation.error}") {
            activation.error shouldBe null
        }
        driver.bothPass()

        driver.getLifeTotal(opponent) shouldBe 10
        driver.getLifeTotal(player) shouldBe 30
        withClue("all ten Elves plus Lathril are tapped") {
            elves.all { driver.isTapped(it) } shouldBe true
            driver.isTapped(lathril) shouldBe true
        }
    }

    test("nine other Elves is not enough — Lathril does not count toward the ten") {
        val driver = createDriver()
        val player = driver.player1
        val opponent = driver.player2

        val (lathril, elves) = setUpElves(driver, player, otherElves = 9)

        val nineOnly = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = lathril,
                abilityId = drainAbilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = elves)
            )
        )
        withClue("nine Elves cannot pay a tap-ten cost") { (nineOnly.error != null) shouldBe true }

        // …and Lathril may not be used to round the group up to ten (excludeSelf).
        val nineAndLathril = driver.submit(
            ActivateAbility(
                playerId = player,
                sourceId = lathril,
                abilityId = drainAbilityId,
                costPayment = AdditionalCostPayment(tappedPermanents = elves + lathril)
            )
        )
        withClue("Lathril herself does not count toward the ten") {
            (nineAndLathril.error != null) shouldBe true
        }
        withClue("no life changed hands") {
            driver.getLifeTotal(opponent) shouldBe 20
            driver.getLifeTotal(player) shouldBe 20
        }
        withClue("Lathril was not tapped by a failed activation") {
            driver.isTapped(lathril) shouldBe false
        }
    }
})
